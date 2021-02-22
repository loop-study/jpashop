package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Data
    @AllArgsConstructor
    static class Result<T> {
        //private int count;
        private T data;
    }

}
