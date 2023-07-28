package com.example.ecommerceproject.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.repository.ItemRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ScheduledServiceTest {

  @Autowired
  private ScheduledService scheduledService;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

//  @Test
//  public void deleteOldItems(){
//    // Given
//    Item item = Item.builder()
//        .id(1L)
//        .sellerId(1L)
//        .itemName("테스트 네임1")
//        .price(1000)
//        .itemDetail("테스트 상품입니다.")
//        .cateGory(Category.FOOD)
//        .saleStatus(ItemSellStatus.SOLD_OUT).build();
//
//    itemRepository.save(item);
//    item.setCreatedAt(LocalDateTime.now().minusYears(3).minusDays(1));
//    item.setUpdatedAt(LocalDateTime.now().minusYears(3).minusDays(1));
//
//    // when
//    scheduledService.deleteItemNotSoldForThreeYears();
//
//    // Then
//    List<Item> items = itemRepository.findAll();
//    assertThat(items.size()).isEqualTo(0);
//  }

  @Test
  public void deleteOldItems(){
    // Given
    String sql = "INSERT INTO item (item_id, seller_id, item_name, price, item_detail, cate_gory, sale_status, created_at, updated_at) " +
        "VALUES (1, 1, '테스트 네임1', 1000, '테스트 상품입니다.', 'FOOD', 'SOLD_OUT', ?, ?)";

    LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3).minusDays(1);
    jdbcTemplate.update(sql, Timestamp.valueOf(threeYearsAgo), Timestamp.valueOf(threeYearsAgo));

    // When
    scheduledService.deleteItemNotSoldForThreeYears();

    // Then
    List<Item> items = itemRepository.findAll();
    assertThat(items.size()).isEqualTo(0);
  }
}