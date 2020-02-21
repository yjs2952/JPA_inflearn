package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * 엔티티를 직접 노출한다. 절대 하지 마라
     */
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();

            orderItems
                    .forEach( o -> o.getItem().getName());

            // order, orderItems 강제 초기화....
            // hibernate5module 의 기본 전략이 lazy 이기 때문
        }

        return all;
    }
}
