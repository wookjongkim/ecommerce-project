package com.example.ecommerceproject.domain.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawDto {

  @NotNull(message = "출금할 금액을 입력해주세요!")
  @Min(value = 1000, message = "출금할 금액을 최소 천원 이상 입력해주세요.")
  private Long amount; // 출금할 금액
}
