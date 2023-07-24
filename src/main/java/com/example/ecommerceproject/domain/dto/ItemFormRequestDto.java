package com.example.ecommerceproject.domain.dto;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class ItemFormRequestDto {

  // 등록하는 판매자의 식별자
  private Long sellerId;

  @NotBlank(message = "상품명은 필수 입력값입니다.")
  private String itemName;

  @NotNull(message = "가격은 필수 입력 값 입니다.")
  @Range(min = 100, max = 10000000, message = "가격은 백원 이상 천만원 이하여야 합니다.")
  private Integer price;

  @NotNull(message = "재고는 필수 입력값입니다.")
  @Max(value = 9999, message = "재고는 최대 9999개 까지 등록 가능합니다.")
  @Min(value = 0, message = "재고는 최소 0개 이상이여야 합니다.")
  private Integer stockNumber;

  @NotBlank(message = "상품 설명은 필수 입력값입니다.")
  private String itemDetail;

  private Category category;

  @NotNull(message = "판매 상태는 필수 입력 값입니다.")
  private ItemSellStatus itemSellStatus;
}
