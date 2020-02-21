package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * xToOne(ManyToOne, OneToOne)
 * ORder
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {

        /* 엔티티를 외부에 노출하지 마라 그냥 */
        // 양방향 관계인 엔티티들이 무한 루프를 발생시킨다.
        // lazy 전략으로 맵핑된 엔티티들이 프록시 객체로 생성된다. (byteBuddy 라이브러리 사용)
        // 귀찮다고 eager 로 바꾸지 말 것 (성능 튜닝이 매우 어려워진다.)
        // 양방향 맵핑된 엔티티들을 @jsonIgnore 시켜도 잭슨은 프록시 객체를 다루지 못하기 때문에 에러 발생
        // 잭슨에게 하이버네이트에서 만든 객체는 처리하지 않도록 하려면 하이버네잍트5 모듈 라이브러리를 사용해야한다.
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();    // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        return orderRepository.findAllByCriteria(new OrderSearch())
        .stream().map(SimpleOrderDto::new).collect(toList());
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        return orderRepository.findAllWithMemberDelivery()
                .stream().map(SimpleOrderDto::new).collect(toList());
    }

    /**
     * v3, v4 둘 다 성능 차이가 별로 나지 않는다.
     * 단순 조회만을 위한 쿼리, 재사용 불가능, 엔티티가 아니기 때문에 영속성 관리 불가능
     * 그러나 엔티티에 컬럼이 엄청나게 많고 트래픽도 많다면 dto 사용을 고민해 봐야 한다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
