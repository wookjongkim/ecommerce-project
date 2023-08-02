package com.example.ecommerceproject.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

  @Id
  @Column(name = "order_item_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long itemId;

  private String name;

  private int price;

  private int quantity;

  public static OrderItem of(Item item, int quantity){
    return OrderItem.builder()
        .itemId(item.getId())
        .name(item.getItemName())
        .price(item.getPrice())
        .quantity(quantity)
        .build();
  }
}
