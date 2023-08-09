package com.example.ecommerceproject.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.domain.model.Stock;
import com.example.ecommerceproject.repository.spec.ItemSpecification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private MemberRepository memberRepository;

  private Member member;

  @BeforeEach
  void regMember() {
    member = Member.builder()
        .role(Role.SELLER)
        .build();
    memberRepository.save(member);
  }

  @Test
  @DisplayName("판매자의 전체 상품 조회")
  public void lookupItems() {
    Stock stock = Stock.builder()
        .quantity(10).build();

    for (int i = 0; i < 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(10000 + i)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();

      itemRepository.save(item);
    }

    List<Item> itemList = itemRepository.findAll(
        Specification.where(ItemSpecification.withSellerId(member.getId()))
    );

    assertEquals(10, itemList.size());
  }

  @Test
  @DisplayName("판매자의 상품을 재고 내림차순으로 조회")
  public void lookupItemsByStockDesc() {

    for (int i = 1; i <= 10; i++) {
      Stock stock = Stock.builder()
          .quantity(i).build();

      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(10000 + i)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();

      itemRepository.save(item);
    }

    List<Item> itemList = itemRepository.findAll(
        Specification.where(ItemSpecification.withSellerId(member.getId()))
            .and(ItemSpecification.withQuantityOrder("desc"))
    );

    int previousQuantity = Integer.MAX_VALUE;
    for (Item item : itemList) {
      int currentQuantity = item.getStock().getQuantity();
      assertTrue(previousQuantity >= currentQuantity);
      previousQuantity = currentQuantity;
    }
  }

  @Test
  @DisplayName("판매자의 상품을 재고 오름차순으로 조회")
  public void lookupItemsByStockAsc() {

    for (int i = 1; i <= 10; i++) {
      Stock stock = Stock.builder()
          .quantity(i).build();

      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(10000 + i)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();

      itemRepository.save(item);
    }

    List<Item> itemList = itemRepository.findAll(
        Specification.where(ItemSpecification.withSellerId(member.getId()))
            .and(ItemSpecification.withQuantityOrder("asc"))
    );

    int previousQuantity = Integer.MIN_VALUE;

    for (Item item : itemList) {
      int currentQuantity = item.getStock().getQuantity();
      assertTrue(previousQuantity <= currentQuantity);
      previousQuantity = currentQuantity;
    }
  }

  @Test
  @DisplayName("판매자의 상품을 가격 범위로 조회")
  public void lookupItemsByPrice() {

    Stock stock = Stock.builder()
        .quantity(1).build();

    for (int i = 1; i <= 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();

      itemRepository.save(item);
    }

    List<Item> itemList = itemRepository.findAll(
        Specification.where(ItemSpecification.withSellerId(member.getId()))
            .and(ItemSpecification.withPriceBetween(10000, 30000))
    );

    assertEquals(3, itemList.size());
  }

  @Test
  @DisplayName("판매자의 상품을 판매 상태를 바탕으로 조회")
  public void lookupItemsBySellStatus() {

    Stock stock = Stock.builder()
        .quantity(1).build();

    // 3가지 판매 상태에 따른 물품 등록

    for (int i = 1; i <= 5; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();

      itemRepository.save(item);
    }

    for (int i = 6; i <= 13; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SOLD_OUT)
          .stock(stock)
          .build();

      itemRepository.save(item);
    }

    for (int i = 13; i <= 15; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL_STOPPED)
          .stock(stock)
          .build();

      itemRepository.save(item);
    }

    List<Item> itemList = itemRepository.findAll(
        Specification.where(ItemSpecification.withSellerId(member.getId()))
            .and(ItemSpecification.withSaleStatus(ItemSellStatus.SELL))
    );

    assertEquals(5, itemList.size());
  }

  @Test
  @DisplayName("판매자의 상품을 등록일자 범위로 조회")
  public void lookupItemsByDateTime() {

    Stock stock = Stock.builder()
        .quantity(1).build();

    LocalDateTime startDate = LocalDate.of(2023, 1, 8).atStartOfDay();
    LocalDateTime endDate = LocalDate.of(2023, 5, 8).atTime(23, 59, 59);

    for (int i = 0; i < 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i)
          .itemDetail("test detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();

      itemRepository.save(item);

      // 저장 전에(save(item)) 이 item.setCreatedAt()을 통해 등록일자를 바꾸었을때 save 시 @CreatedDate로 인해
      // 테스트 당시 시간이 설정되었음. 따라서 save를 통해 영속화를 하고, set을 해주었을때 따로 save를 하지 않아도
      // 더티 체킹으로 인해 등록일짜가 바뀜
      item.setCreatedAt(startDate.plusMonths(i));
    }

    List<Item> itemList = itemRepository.findAll(
        Specification.where(ItemSpecification.withSellerId(member.getId()))
            .and(ItemSpecification.withCreatedDateBetween(startDate, endDate))
    );

    assertEquals(5, itemList.size());
  }


}