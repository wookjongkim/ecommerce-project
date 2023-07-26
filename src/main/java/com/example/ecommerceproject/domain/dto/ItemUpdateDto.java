package com.example.ecommerceproject.domain.dto;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.model.Item;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

  // 상품 수정에 사용될 dto
  @NotBlank(message = "상품명은 필수 입력값입니다.")
  private String itemName;

  @NotNull(message = "가격은 필수 입력 값 입니다.")
  @Range(min = 100, max = 10000000, message = "가격은 백원 이상 천만원 이하여야 합니다.")
  private int price;

  @NotBlank(message = "상품 설명은 필수 입력값입니다.")
  private String itemDetail;

  @NotNull(message = "판매 상태는 필수 입력 값입니다.")
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
