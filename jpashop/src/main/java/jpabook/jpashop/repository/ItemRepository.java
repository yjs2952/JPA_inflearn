package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            /*
             * merge 를 사용하면 파라미터로 넘겨받은 객체를 영속성 컨텍스트에서 찾아보고 없으면 DB 에서 조회를 한다.
             * 조회한 엔티티를 넘겨받은 파라미터 값으로 모두 자동으로 세팅을 한다.
             * 값이 세팅된 엔티티가 리턴된다.
             * 트랜잭션이 커밋되는 시점에 변경감지(dirty checking)가 발동하여 update 쿼리를 실행한다.
             *
             * 주의사항 : merge 는 준영속 엔티티의 모든 필드를 교체한다.
             *          따라서 만약 세팅된 값이 없는 필드가 있으면 null 로 세팅되어 업데이트 될 위험이 있다.
             *          실무에서 merge 를 사용해선 안된다.
             */
            em.merge(item); // 이미 있는 entity 를 업데이트(와 비슷)
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

}
