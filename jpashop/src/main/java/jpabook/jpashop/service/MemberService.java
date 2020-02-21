package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

//  @Autowired  // 필드 주입 단점 : 접근이 안되기 때문에 외부에서 바꿀 수가 없다. (테스트 할 때 교체가 안됨)
//  바뀔일이 없기 때문에 final 을 쓰는게 좋다. 게다가 컴파일 시점에 초기화 여부를 체크할 수 있다.
    private final MemberRepository memberRepository;

//      생성자 주입 권장 : 해당 객체가 무엇을 주입받아야 하는지 명확하고 테스트 시 mock 객체 치환이 용이하다.
//      최신 스프링 버전에서는 생성자가 한 개인 경우에 @Autowired 없어도 자동으로 주입해 준다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//      setter 주입 장점 : 테스트 할 때 mock 객체를 주입해서 사용하기 편하다
//      단점 : 런타임 시 누군가가 바꿀 수가 있다. 일반적으로 어플리케이션 로딩 시점에 빈 세팅이 이미 다 끝나있다.
//              따라서 어플리케이션 실행중에 바뀔 일이 없다. 그닥 좋은 방법이 아니다.
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 회원가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);   // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findMember(member.getName());

        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
