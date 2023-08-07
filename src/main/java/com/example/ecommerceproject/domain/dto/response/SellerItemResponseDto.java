package com.example.ecommerceproject.domain.dto.response;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerItemResponseDto {

  // 리스트 조회시 응답으로 내려보낼 형태

  private Long id; // 상품 아이디

  private String itemName; // 상품 명

  private ItemSellStatus itemSellStatus; // 상품 상태

  private int quantity; // 수량

  private Category category; // 카테고리

  private int price; // 가격

  public static SellerItemResponseDto of(Item item){
    return SellerItemResponseDto.builder()
        .id(item.getId())
        .itemName(item.getItemName())
        .itemSellStatus(item.getSaleStatus())
        .quantity(item.getStock().getQuantity())
        .category(item.getCateGory())
        .price(item.getPrice())
        .build();
  }
}
