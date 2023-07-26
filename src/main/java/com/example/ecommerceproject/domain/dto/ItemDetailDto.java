package com.example.ecommerceproject.domain.dto;

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
public class ItemDetailDto {
  // 상품 상세 정보 조회 및 수정에 사용될 dto
  private String itemName;
  private int price;
  private String itemDetail;
  private ItemSellStatus saleStatus;
  private Category category;

  public static ItemDetailDto of(Item item){
    return ItemDetailDto.builder()
        .itemName(item.getItemName())
        .price(item.getPrice())
        .itemDetail(item.getItemDetail())
        .saleStatus(item.getSaleStatus())
        .category(item.getCateGory())
        .build();
  }
}
