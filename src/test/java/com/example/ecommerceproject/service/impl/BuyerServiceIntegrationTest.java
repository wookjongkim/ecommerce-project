package com.example.ecommerceproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.response.BuyerItemResponseDto;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.domain.model.Stock;
import com.example.ecommerceproject.repository.ItemRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import com.example.ecommerceproject.service.BuyerService;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class BuyerServiceIntegrationTest {

  @Autowired
  private BuyerService buyerService;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private MemberRepository memberRepository;

  private Member member;
  private Stock stock;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .role(Role.SELLER)
        .build();
    memberRepository.save(member);

    stock = Stock.builder()
        .quantity(100)
        .build();
  }

  @Test
  @DisplayName("구매자의 전체 상품 조회(모든 조건 적용 X)")
  public void lookupItems() {
    //Given
    for (int i = 0; i < 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(10000 + i)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "";
    Integer minPrice = 0;
    Integer maxPrice = Integer.MAX_VALUE;
    String priceOrder = "asc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SELL;
    Category category = null;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    // Then
    assertEquals(10, itemPage.getContent().size());
    assertTrue(itemPage.getContent().stream().allMatch(dto -> dto.getItemName().startsWith("Test Name")));
    assertTrue(itemPage.getContent().stream().allMatch(dto -> dto.getItemSellStatus().equals(ItemSellStatus.SELL)));
  }

  @Test
  @DisplayName("구매자가 상품 이름을 바탕으로 상품 조회")
  public void lookupItemsByItemNm() {
    //Given
    for (int i = 0; i < 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(10000 + i)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "Test Name";
    Integer minPrice = 0;
    Integer maxPrice = Integer.MAX_VALUE;
    String priceOrder = "asc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SELL;
    Category category = null;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    // Then
    assertEquals(10, itemPage.getContent().size());
    assertTrue(itemPage.getContent().stream().allMatch(dto -> dto.getItemName().startsWith("Test Name")));
    assertTrue(itemPage.getContent().stream().allMatch(dto -> dto.getItemSellStatus().equals(ItemSellStatus.SELL)));
  }

  @Test
  @DisplayName("구매자가 가격 범위를 바탕으로 상품 조회")
  public void lookupItemsByPriceRange() {
    //Given
    for (int i = 0; i < 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "Test Name";
    Integer minPrice = 10000;
    Integer maxPrice = 50000;
    String priceOrder = "asc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SELL;
    Category category = null;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    List<BuyerItemResponseDto> itemList = itemPage.getContent();

    // Then
    assertEquals(5, itemList.size());
    assertTrue(itemList.stream().allMatch(dto -> dto.getPrice() >= minPrice && dto.getPrice() <= maxPrice));
  }

  @Test
  @DisplayName("구매자가 가격 오름차순을 바탕으로 상품조회")
  public void lookupItemsByPriceAsc() {
    //Given
    for (int i = 0; i < 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "";
    Integer minPrice = 0;
    Integer maxPrice = Integer.MAX_VALUE;
    String priceOrder = "asc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SELL;
    Category category = null;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    List<BuyerItemResponseDto> itemList = itemPage.getContent();

    // Then
    assertEquals(10, itemList.size());
    assertTrue(IntStream.range(0, itemList.size()-1)
        .allMatch(i -> itemList.get(i).getPrice() <= itemList.get(i+1).getPrice()));
  }

  @Test
  @DisplayName("구매자가 가격 내림차순을 바탕으로 상품 조회")
  public void lookupItemsByPriceDesc() {
    //Given
    for (int i = 0; i < 10; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "";
    Integer minPrice = 0;
    Integer maxPrice = Integer.MAX_VALUE;
    String priceOrder = "desc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SELL;
    Category category = null;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    List<BuyerItemResponseDto> itemList = itemPage.getContent();

    // Then
    assertEquals(10, itemList.size());
    assertTrue(IntStream.range(0, itemList.size()-1)
        .allMatch(i -> itemList.get(i).getPrice() >= itemList.get(i+1).getPrice()));
  }

  @Test
  @DisplayName("구매자가 판매 상태(SOLD_OUT)를 바탕으로 상품 조회")
  public void lookupItemsBySoldOut() {
    //Given
    for (int i = 0; i < 5; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SOLD_OUT)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    for (int i = 6; i < 13; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "";
    Integer minPrice = 0;
    Integer maxPrice = Integer.MAX_VALUE;
    String priceOrder = "asc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SOLD_OUT;
    Category category = null;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    List<BuyerItemResponseDto> itemList = itemPage.getContent();

    // Then
    assertEquals(5, itemList.size());
    assertTrue(IntStream.range(0, itemList.size()-1)
        .allMatch(i -> itemList.get(i).getItemSellStatus().equals(ItemSellStatus.SOLD_OUT)));
  }

  @Test
  @DisplayName("구매자가 판매 상태(SELL_STOPPED)를 바탕으로 상품 조회")
  public void lookupItemsBySellStopped() {
    //Given
    for (int i = 0; i < 5; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL_STOPPED)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    for (int i = 6; i < 13; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.FOOD)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "";
    Integer minPrice = 0;
    Integer maxPrice = Integer.MAX_VALUE;
    String priceOrder = "asc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SELL_STOPPED;
    Category category = null;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    List<BuyerItemResponseDto> itemList = itemPage.getContent();

    // Then
    assertEquals(5, itemList.size());
    assertTrue(IntStream.range(0, itemList.size()-1)
        .allMatch(i -> itemList.get(i).getItemSellStatus().equals(ItemSellStatus.SELL_STOPPED)));
  }

  @Test
  @DisplayName("구매자가 카테고리를 바탕으로 상품 조회")
  public void lookupItemsByCategory() {
    //Given
    for (int i = 0; i < 5; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.SPORTS)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    for (int i = 6; i < 13; i++) {
      Item item = Item.builder()
          .sellerId(member.getId())
          .itemName("Test Name" + i)
          .price(i * 10000)
          .itemDetail("Test Detail")
          .cateGory(Category.BEAUTY)
          .saleStatus(ItemSellStatus.SELL)
          .stock(stock)
          .build();
      itemRepository.save(item);
    }

    String component = "";
    Integer minPrice = 0;
    Integer maxPrice = Integer.MAX_VALUE;
    String priceOrder = "asc";
    ItemSellStatus itemSellStatus = ItemSellStatus.SELL;
    Category category = Category.SPORTS;
    Integer page = 0;
    Integer size = 20;

    Pageable pageable = PageRequest.of(page, size,
        Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    // When
    Page<BuyerItemResponseDto> itemPage = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, itemSellStatus, category,
        pageable);

    List<BuyerItemResponseDto> itemList = itemPage.getContent();

    // Then
    assertEquals(5, itemList.size());
    assertTrue(IntStream.range(0, itemList.size()-1)
        .allMatch(i -> itemList.get(i).getCategory().equals(Category.SPORTS)));
  }
}
