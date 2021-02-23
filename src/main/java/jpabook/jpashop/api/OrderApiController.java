package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderDto;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // OSIV 종료로 변환로직을 모두 OrderQueryService 로 이전
    private final OrderQueryService orderQueryService;

    // 안 좋은 케이스 1. 엔티티 노출
    // JsonIgnore 양방향에 걸어줘야한다.
    @GetMapping("/api/v1/orders")
    public Result ordersV1() {
        // 무한 반복.. 하이버5모듈로 막아둠.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

//            List<OrderItem> orderItems = order.getOrderItems();
//            for (OrderItem orderItem : orderItems) {
//                orderItem.getItem().getName();
//            }
            // 람다로 간단히.
            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
        }
        return new Result(all);
    }

    // 쿼리 조회가 1 + n + n + n....
    // 성능 가장 큰 문제
    /**
     {"data":[{"orderId":4,"name":"userA","orderDate":"2021-02-21T15:48:57.389526","orderStatus":"ORDER","address":{"city":"인천","street":"1","zipcode":"1111"},"orderItems":[{"name":"Object1 book","orderPrice":10000,"count":1},{"name":"Object2 book","orderPrice":20000,"count":2}]},{"orderId":11,"name":"userA","orderDate":"2021-02-21T15:48:57.426661","orderStatus":"ORDER","address":{"city":"진주","street":"2","zipcode":"2222"},"orderItems":[{"name":"SPRING1 book","orderPrice":20000,"count":3},{"name":"SPRING2 book","orderPrice":40000,"count":4}]}]}
     */
//    @GetMapping("/api/v2/orders")
//    public Result ordersV2() {
//        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
//        List<OrderQueryDto> collect = orders.stream()
//                .map(o -> new OrderQueryDto(o))
//                .collect(toList());
//        return new Result(collect);
//    }

    // jpql 을 사용해서 한번의 쿼리로 조회되었지만
    // 엔티티가 반환된다.
    // 조인으로 인해 위와 다르게 주문이 2배나 조회되었다. orderItems 수 만큼 주문 수가 뻥튀기됨
    // distinct 로 중복 주문조회 처리함.
    // -> distinct 로 작성된 실행 쿼리를 db 에서 실행하면 뻥튀기로 조회됨.
    // -> jpa 에서 자체적으로 중복을 제거해줌
    /**
     {"data":[{"id":4,"member":{"id":1,"name":"userA","address":{"city":"인천","street":"1","zipcode":"1111"}},"orderItems":[{"id":6,"item":{"id":2,"name":"Object1 book","price":35000,"stockQuantity":99,"categories":null,"author":null,"isbn":null},"orderPrice":10000,"count":1,"totalPrice":10000},{"id":7,"item":{"id":3,"name":"Object2 book","price":45000,"stockQuantity":198,"categories":null,"author":null,"isbn":null},"orderPrice":20000,"count":2,"totalPrice":40000}],"delivery":{"id":5,"address":{"city":"인천","street":"1","zipcode":"1111"},"status":null},"orderDate":"2021-02-21T15:48:57.389526","status":"ORDER","totalPrice":50000},{"id":4,"member":{"id":1,"name":"userA","address":{"city":"인천","street":"1","zipcode":"1111"}},"orderItems":[{"id":6,"item":{"id":2,"name":"Object1 book","price":35000,"stockQuantity":99,"categories":null,"author":null,"isbn":null},"orderPrice":10000,"count":1,"totalPrice":10000},{"id":7,"item":{"id":3,"name":"Object2 book","price":45000,"stockQuantity":198,"categories":null,"author":null,"isbn":null},"orderPrice":20000,"count":2,"totalPrice":40000}],"delivery":{"id":5,"address":{"city":"인천","street":"1","zipcode":"1111"},"status":null},"orderDate":"2021-02-21T15:48:57.389526","status":"ORDER","totalPrice":50000},{"id":11,"member":{"id":1,"name":"userA","address":{"city":"인천","street":"1","zipcode":"1111"}},"orderItems":[{"id":13,"item":{"id":9,"name":"SPRING1 book","price":20000,"stockQuantity":97,"categories":null,"author":null,"isbn":null},"orderPrice":20000,"count":3,"totalPrice":60000},{"id":14,"item":{"id":10,"name":"SPRING2 book","price":40000,"stockQuantity":96,"categories":null,"author":null,"isbn":null},"orderPrice":40000,"count":4,"totalPrice":160000}],"delivery":{"id":12,"address":{"city":"진주","street":"2","zipcode":"2222"},"status":null},"orderDate":"2021-02-21T15:48:57.426661","status":"ORDER","totalPrice":220000},{"id":11,"member":{"id":1,"name":"userA","address":{"city":"인천","street":"1","zipcode":"1111"}},"orderItems":[{"id":13,"item":{"id":9,"name":"SPRING1 book","price":20000,"stockQuantity":97,"categories":null,"author":null,"isbn":null},"orderPrice":20000,"count":3,"totalPrice":60000},{"id":14,"item":{"id":10,"name":"SPRING2 book","price":40000,"stockQuantity":96,"categories":null,"author":null,"isbn":null},"orderPrice":40000,"count":4,"totalPrice":160000}],"delivery":{"id":12,"address":{"city":"진주","street":"2","zipcode":"2222"},"status":null},"orderDate":"2021-02-21T15:48:57.426661","status":"ORDER","totalPrice":220000}]}
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        // 뻥튀기 된 주문내역 출력해보면 같은 객체인걸 알 수 있음...
        // distinct 로 해결.
        // v2 버전과 같은 로직임. fetch, distinct 의 차이일뿐 ㄷㄷㄷ
        // 치명적인 단점 : fetch 조인은 페이징이 불가능함
        // 페이징 조건 보내니 리미트,오프셋이 없음 : HHH000104: firstResult/maxResults specified with collection fetch; applying in memory
        // 전체 조회 후 메모리에서 페이징 처리함...
//        return new Result(orderRepository.findAllWithItem().stream()
//                    .map(o -> new OrderQueryDto(o))
//                    .collect(toList()));

        // OSIV 종료 후 수정된 방법
//        return new Result(orderQueryService.orderV3());
        return orderQueryService.orderV3();
    }

    // 페이징 한계 돌파, hibernate.default_batch_fetch_size 사용
//    @GetMapping("/api/v3.1/orders")
//    public Result orderV3_page(
//            @RequestParam(value = "offset", defaultValue = "0") int offset,
//            @RequestParam(value = "limit", defaultValue = "100") int limit) {
//        return new Result(orderRepository.findAllWithMemberDelivery(offset, limit).stream()
//                .map(o -> new OrderQueryDto(o))
//                .collect(toList()));
//    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // 쿼리 한번으로 데이터를 다 가져옴.
    // 단점 : 메모리상에서 하나하나 재정리하는 작업이 필요함. 데이터가 많아지면 V5 보다 느려짐
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {

        // OrderFlatDto 스펙이 아니라 OrderQueryDto 로 맞춰야한다면?
//        return orderQueryRepository.findAllByDto_flat();

        // 하나하나 발라서 넣어준다
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        // 노가다 노가다 노가다... 순서가 바뀜, sort 필요하면 넣어줄것
        // order 기준으로 페이징이 안됨
        // 이전 회사에서 주문상품 기준으로 페이징했었징...
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                        o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()), mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                        o.getItemName(), o.getOrderPrice(), o.getCount()), toList()) )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());

//        return flats.stream()
//                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
//                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
//                )).entrySet().stream()
//                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
//                .collect(toList());
    }


    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

//    @Data
//    static class OrderDto {
//        private Long orderId;
//        private String name;
//        private LocalDateTime orderDate;
//        private OrderStatus orderStatus;
//        private Address address;
//        // DTO 안에 엔티티가 있으면 안됨.
//        // 완전히 엔티티에 대한 의존을 끊어야함.
////        private List<OrderItem> orderItems;
//        private List<OrderItemDto> orderItems;
//
//        public OrderDto(Order order) {
//            this.orderId = order.getId();
//            this.name = order.getMember().getName();
//            this.orderDate = order.getOrderDate();
//            this.orderStatus = order.getStatus();
//            this.address = order.getDelivery().getAddress();
//            // orderItems 널이라 초기화
////            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            this.orderItems = order.getOrderItems().stream()
//                        .map(orderItem-> new OrderItemDto(orderItem))
//                        .collect(toList());
//        }
//    }

//    @Getter
//    static class OrderItemDto {
//
//        private String name; // 상품명
//        private int orderPrice; // 주문 가격
//        private int count; // 주문 수
//
//        public OrderItemDto(OrderItem orderItem) {
//            this.name = orderItem.getItem().getName();
//            this.orderPrice = orderItem.getOrderPrice();
//            this.count = orderItem.getCount();
//        }
//    }
}
