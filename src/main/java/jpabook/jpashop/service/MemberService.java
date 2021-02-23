package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //final 필드만 가지고 생성자로 만들어줌
//@Transactional    //데이터 저장관련은 트랜잭션 필수.
public class MemberService {

    //@Autowired -> 필드에서 세터 방식으로 변경, -> 궁극적으로 생성자 인젝션 변경 -> lombok 으로 간단하게 처리함.
    public final MemberRepository memberRepository;

//    @Autowired    //setter injection
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //==주요 로직==//
    //회원 가입
    @Transactional
    public Long join(Member member) {
        //중복 회원 로직 추가해보자
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    //중복 회원 검증
    private void validateDuplicateMember(Member member) {
        //EXCEPTION, 실무에선 문제있음. 동시성 문제. 유니크해야하면 컬럼에 유니크로 걸어준다.
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    //회원 전체 조회
    @Transactional(readOnly = true) //JPA 가 읽기전용엔 성능 최적화를 해줌. readOnly = false default 임.
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //회원 한명 조회
    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
//        return memberRepository.findOne(memberId);
        return memberRepository.findById(memberId).get();
    }


    // member 반환을 안하나? 개발자마다 철학차이?
    // 이 강의에서는 커맨드랑 쿼리를 철저히 분리하자
    @Transactional
    public void update(Long id, String name) {
        // 변경 감지를 이용한 update 처리.
        Member member = memberRepository.findById(id).get();
        member.setName(name);
    }
}
