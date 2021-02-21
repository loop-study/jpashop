package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 총 2주문 2개
 * * userA
 *   * JPA1 BOOK
 *   * JPA2 BOOK
 * * userB
 *   * SPRING1 BOOK
 *   * SPRING2 BOOK
 */
@Component  // spring scan 대상이 됨.
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    // 스프링 실행 시점에 호출하기 위해서 사용함
    @PostConstruct
    public void init() {
        // 여기에 코드 넣어도 되지 않은가? 스프링 라이프 사이클때문에 트랜잭션 같은 부분에서 문제가 생김.
        // initService.dbInit1(); // 샘플 데이터 추가 후 주석처리
        // initService.dbInit2(); // 샘플 데이터 추가 후 주석처리

    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        public void dbInit1() {
            Member member = createMember("userA", "인천", "1", "1111");
            em.persist(member);

            Book book1 = createBook("Object1 book", 35000, 100);
            em.persist(book1);

            Book book2 = createBook("Object2 book", 45000, 200);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            // createOrder 에서 ... 한 이유!! 주문 여러개 넘어가짐
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Book createBook(String s, int i, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(s);
            book1.setPrice(i);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }

        public void dbInit2() {
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 book", 20000, 100);
            em.persist(book1);

            Book book2 = createBook("SPRING2 book", 40000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            // createOrder 에서 ... 한 이유!! 주문 여러개 넘어가짐
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Member createMember(String username, String city, String s, String s2) {
            Member member = new Member();
            member.setName(username);
            member.setAddress(new Address(city, s, s2));
            return member;
        }
    }
}
