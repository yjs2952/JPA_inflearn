package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    /**
     * 엔티티는 절대 API 로 외부에 노출해서는 안된다. 반드시 dto 로 변환해서 전송할 것
     * 민감한 정보가 노출될 수도 있으며 엔티티에 비즈니스 로직이 변하는 순간 API 스펙이 변해버린다.
     * 굉장히 불안정한 API 가 될 수 있기에 반드시 엔티티는 dto 로 변환해서 내보내야한다.
     * 다만, 서버사이드 렌더링인 템플릿 엔진으로 보내는 경우는 괜찮지만 그래도 dto 로 변환해서 보내기를 권장한다.
     */
}
