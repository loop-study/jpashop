package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemDto {

    private String name; // 상품명
    private int orderPrice; // 주문 가격
    private int count; // 주문 수

    public OrderItemDto(OrderItem orderItem) {
        this.name = orderItem.getItem().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }
}
