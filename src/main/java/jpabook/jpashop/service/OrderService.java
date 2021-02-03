package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // entity select
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // delivery create
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // orderItem create
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // order create
        Order order = Order.createOrder(member, delivery, orderItem);

        // order save - 주문 하나만 저장했네?
        // Order 안을 보면 OrderItem, Delivery 가 cascade=ALL 적용되어있어서 연관된 걸 다 persist 해줌
        // 대신 다른곳에도 얽혀있으면 cascade 사용하면 안된다. 꼬여서 의도치않게 변경될 경우가 많다
        orderRepository.save(order);

        return order.getId();
    }


    /**
     * 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
