package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d ", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithItem(){
        /**
         * jpa 의 distinct는 SQL 문의 distinct 와 동일한 역할을 하며
         * 식별자가 같은 엔티티를 걸러준다.
         * 컬렉션을 fetch join 해버리면 페이징을 할 수 없다.
         * 페이징을 써버리면 하이버네이트가 DB 에서 모든 데이터를 메모리로 읽어와서 그 안에서 페이징을 해 버린다. (굉장히 위험하다.)
         */
        return em.createQuery(
                "select distinct o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item", Order.class
                ).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit){
        /**
         * jpa 의 distinct는 SQL 문의 distinct 와 동일한 역할을 하며
         * 식별자가 같은 엔티티를 걸러준다.
         * 컬렉션을 fetch join 해버리면 페이징을 할 수 없다.
         * 페이징을 써버리면 하이버네이트가 DB 에서 모든 데이터를 메모리로 읽어와서 그 안에서 페이징을 해 버린다. (굉장히 위험하다.)
         */
        return em.createQuery(
                "select distinct o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d ", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}
