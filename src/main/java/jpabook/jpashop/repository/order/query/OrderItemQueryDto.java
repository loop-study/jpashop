package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.OrderItem;
import lombok.Data;

@Data
public class OrderItemQueryDto {

    @JsonIgnore
    private Long orderId; // 주문번호
    private String name; // 상품명
    private int orderPrice; // 주문 가격
    private int count; // 주문 수

    public OrderItemQueryDto(Long orderId, String name, int orderPrice, int count) {
        this.orderId = orderId;
        this.name = name;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
