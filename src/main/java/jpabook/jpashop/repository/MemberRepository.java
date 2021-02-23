package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 스프링 데이터 JPA 사용 방법
public interface MemberRepository extends JpaRepository<Member, Long> {

    // jpql 작성해줘야지...?
    // 스프링데이터JPA 에선 자동으로 아래의 쿼리를 만들어준다
    // select m from Member m where m.name = ?
    List<Member> findByName(String name);
}
