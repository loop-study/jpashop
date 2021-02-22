package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // 나쁜 예제. 에러만 뿜뿜, 절대 하면 안되는 방법
    @GetMapping("/api/v1/sample-orders")
    public Result orderV1() {
        // 무한 루프.... 무한 반환;;;?????
        // json으로 반환하려고 보니
        // Order 안에 Member 가 있고 Member 안에 Order 가 있고... 무한 반복...
        // 양방향 연관관계라면 둘 중 하나를 JsonIgnore 걸어줘야한다!!.

        // @JsonIgnore 걸고나서 에러 발생.
        // LAZY 지연로딩때문에 발생한 에러? Member 프록시객체다.
        // 잭슨이 프록시를 해석안하게 하면 된다.
        // Hibernate5Module 을 bean 으로 등록하면 해결됨.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화 방법. 조회 됨
            order.getDelivery().getAddress();
        }

        return new Result(all);
    }

    // DTO 로 반환, 권장 방식
    // V1, V2 모두 지연로딩으로 인한 쿼리 조회가 많은 문제점이 존재
    @GetMapping("/api/v2/simple-orders")
    public Result orderV2() {

        // ORDER -> SQL 1번 -> 결과 2개
        // ORDER 2건 조회.
        // 건마다 member, delivery 따로 조회.
        // N + 1 -> 1 + 회원 N + 배송 N
        // LAZY -> EAGER 로 바꾸면 되지 않을까??? 놉놉놉!!! 양방향 영향으로 예측할 수 없는 쿼리가 실행됨.
        // 다음 시간에 할 fetch 조인을 사용해야됨.
        return new Result(orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList()));
    }

    @GetMapping("/api/v3/simple-orders")
    public Result orderV3() {
        // fetch 이용해서 쿼리 한번에 결과 조회됨
        return new Result(orderRepository.findAllWithMemberDelivery().stream()
                .map(SimpleOrderDto::new)
                .collect(toList()));
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }

    // dto 변환 과정 없잉 곧바로 jpa 에서 dto 로 조회해서 반환하기
    // 리포지토리에 위의 dto 클래스 추가해줌
    @GetMapping("/api/v4/simple-orders")
    public Result orderV4() {
        // 로직 변경하기엔 너무 정직됨. -> jpql 쿼리 자체를 수정해야 로직이 변경됨
        // v3 가 좀더 유동적으로 로직 수정이 편함
        // 코드상으론 v3 이 더 낫지만 성능은 v4 가 더 낫다...
        // 뭘 선택해야할까???? 성능테스트해보면 별 차이없다.

        // 쪼갠다
        // orderSimpleQueryRepository : 화면용 데이터 조회용
        // orderRepository : 순수 엔티티 조회용
        // 유지보수용으로 좋아짐
        return new Result(orderSimpleQueryRepository.finOrderDtos());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        //private int count;
        private T data;
    }

}
