package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
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

    public List<Order> findAll() {
        return em.createQuery("select o from Order o join o.member m", Order.class)
//                .setFirstResult(100)    // 페이징 시 스타트 포지션
                .setMaxResults(1000)    // 1000건 가져오기
                .getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        // 만약에 orderSearch 필터가 없으면??
        // 있고 없고에 따라 쿼리를 동적으로 제어해야한다.
        // 마이바티스는 동적쿼리 잘 제어했다. JPA 는...?
//        return em.createQuery("select o from Order o join o.member m" +
//                " where o.status = :status" +
//                " and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
////                .setFirstResult(100)    // 페이징 시 스타트 포지션
//                .setMaxResults(1000)    // 1000건 가져오기
//                .getResultList();


        // 현재 이 방식은 현업에서 사용안함. 아래의 코드 칠 바엔 마이바티스 사용
        String jpql = "select o from Order o join o.member m";

        // 여기서 필터 하나하나 체크하고 jqpl 쿼리문을 수정한다
        Boolean isFirstCondition = true;

        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);
                 // 최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();

    }

    /**
     * JPA Criteria (JPA 표준)
     * 실무에서 사용하지 않는다. 이런게 있구나 하고 넘어가자
     * 유지보수 최악의 코드다... 이게 무슨 쿼리인지 알 수 있나?...
     * querydsl 라이브러리로 동적 쿼리를 제어한다
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" +
                    orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq)
                .setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    //
    public List<Order> findAllWithItem() {
        // queryDsl 쓰면 이렇게 복잡한 쿼리를 작성 안해도 된다
        return em.createQuery(
                    "select distinct o from Order o" +
                            " join fetch o.member m" +
                            " join fetch o.delivery d" +
                            " join fetch o.orderItems oi" +
                            " join fetch oi.item", Order.class)
//                .setFirstResult(1)
//                .setMaxResults(100)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

//    public List<OrderSimpleQueryDto> finOrderDtos() {
//        // dto 로 조회하니 fetch 없네??
//        // Api 스펙이 변경되면 뜯어고쳐야함.
//        return em.createQuery(
//                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto (o.id, m.name, o.orderDate, o.status, d.address)" +
//                        " from Order o" +
//                        " join o.member m" +
//                        " join o.delivery d", OrderSimpleQueryDto.class
//        ).getResultList();
//    }
}
