package com.example.ecommerceproject.domain.dto.response;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerItemResponseDto {

  private String itemName; // 상품 명

  private String itemDetail; // 상품 설명

  private ItemSellStatus itemSellStatus; // 상품 상태

  private int quantity; // 수량

  private Category category; // 카테고리

  private int price; // 가격

  public static BuyerItemResponseDto of(Item item){
    return BuyerItemResponseDto.builder()
        .itemName(item.getItemName())
        .itemDetail(item.getItemDetail())
        .itemSellStatus(item.getSaleStatus())
        .quantity(item.getStock().getQuantity())
        .category(item.getCateGory())
        .price(item.getPrice())
        .build();
  }
}
