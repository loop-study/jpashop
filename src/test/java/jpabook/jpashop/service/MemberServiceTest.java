package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

//@RunWith(SpringRunner.class //jUnit5 에는 @SpringBootTest에 포함되어있음. jUnit4 에서 사용할 것
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @Rollback(false)//롤백되면 insert 쿼리가 안 보임, 롤백은 버린다는 뜻이니.
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("park");

        //when
        Long savedId = memberService.join(member);

        //then
        //em.flush(); //롤백이 있지만 영속성 컨텍스트 쿼리로 변경내용은 디비에 반영해줌, 이후 롤백 진행
        assertEquals(member, memberRepository.findOne(savedId));
    }

    //@Test(expected = IllegalStateException.class) //jUnit5 에는 안됨
    @Test
    public void 중복_회원_예외() throws Exception {

        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        //jUnit5 에서는 expected 못쓰니 이 방식으로 변경해야함.
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertEquals("이미 존재하는 회원입니다", thrown.getMessage());
        //memberService.join(member2);

       /* try {
            memberService.join(member2); //예외가 발생해야한다.
        } catch (IllegalArgumentException e) {
            return;
        }*/

        //then
        Assertions.fail("예외가 발생해야한다.222");
    }
}