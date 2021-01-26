package jpabook.jpashop.domain;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // fetch = FetchType.EAGER default 임. LAZY 로 바꾸자.
    @JoinColumn(name = "member_id")
    private Member member;

    //JPOL select o from order o; -> SQL select * from order 로 번역됨.
    //10000건 있다? 연관테이블이 쭈우우우욱 사이드이펙티드로 n+1 계속 타고타고 날라감.

    @OneToMany(mappedBy = "order", cascade = ALL) //CascadeType.ALL
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    private OrderStatus status; //주문상태 [ORDER, CANCLE]

    //==연관관계 편의 메서드(양방향)==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDeilvery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //이런 양방향 형식을 편의성으로 만들어준다.
//    public static void main(String[] args) {
//        Member member = new Member();
//        Order order = new Order();
//
//        member.getOrders().add(order);
//        order.setMember(member);
//    }
}
