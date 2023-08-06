package com.example.ecommerceproject.domain.dto;

import com.example.ecommerceproject.domain.model.SellerRevenue;
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
