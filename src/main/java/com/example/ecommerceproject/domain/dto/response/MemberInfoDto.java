package com.example.ecommerceproject.domain.dto.response;

import com.example.ecommerceproject.domain.model.Member;
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
public class MemberInfoDto {
  private String name;

  private String email;

  private String address;

  private String phoneNumber;

  private Long balance;

  public static MemberInfoDto of(Member member, Long buyerBalance) {
    return MemberInfoDto.builder()
        .name(member.getName())
        .email(member.getEmail())
        .address(member.getAddress())
        .phoneNumber(member.getPhoneNumber())
        .balance(buyerBalance)
        .build();
  }
}
