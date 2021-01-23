package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        //왜 member가 아닌 id를 반환하지? 사이드이펙트 최소화.
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

}
