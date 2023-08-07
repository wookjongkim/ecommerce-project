package com.example.ecommerceproject.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawResponseDto {

  private Long balance;

  public static WithdrawResponseDto of(Long balance){
    return WithdrawResponseDto.builder()
        .balance(balance)
        .build();
  }
}
