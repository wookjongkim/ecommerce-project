package com.example.ecommerceproject.domain.dto;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateDto {

  private String itemName;

  @Range(min = 100, max = 10000000, message = "가격은 백원 이상 천만원 이하여야 합니다.")
  private Integer price;

  private String itemDetail;

  private ItemSellStatus saleStatus;

  private Category category;

  public static ItemUpdateDto of(Item item){
    return ItemUpdateDto.builder()
        .itemName(item.getItemName())
        .price(item.getPrice())
        .itemDetail(item.getItemDetail())
        .saleStatus(item.getSaleStatus())
        .category(item.getCateGory())
        .build();
  }
}
