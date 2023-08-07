package com.example.ecommerceproject.domain.dto.request;


import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

  @Valid
  @Size(min = 1, message = "한개 이상의 상품을 선택 후 주문을 눌러주세요 :)")
  private List<ItemOrderDto> itemOrders;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ItemOrderDto {
    private long itemId;

    @Min(value = 1, message = "수량은 0보다 큰 숫자로 지정해주세요.")
    private int quantity;
  }
}
