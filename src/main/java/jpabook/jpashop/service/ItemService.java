package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // DTO 로 풀어도 된다. 유지보수 쉽게 더 좋은 설계로 하자
    @Transactional
    public Item updateItem(Long itemId, String name, int price, int quantity) {
 // public Item updateItem(Long itemId, Book bookParam) {
        // 영속상태의 findItem 의 값을 바꿔버림. 준영속을 영속처럼 수정하게함
        // 조회해서 필요한 것만 변경함
        Item findItem = itemRepository.findOne(itemId);
        // findItem.change(name, price, stockQuantity) 처럼 의미있는 메서드로 바꿔야함. 능
        // 안 그러면 역추적 불가
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(quantity);
        return findItem;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
