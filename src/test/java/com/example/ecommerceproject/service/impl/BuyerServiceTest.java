package com.example.ecommerceproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import com.example.ecommerceproject.domain.model.BuyerBalance;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.repository.BuyerBalanceRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class BuyerServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BuyerBalanceRepository buyerBalanceRepository;

  @InjectMocks
  private BuyerServiceImpl buyerService;

  @Test
  void chargeBalance(){
    // given
    Member member = Member.builder()
        .id(1L)
        .name("test")
        .email("test@example.com")
        .password("Test123!")
        .address("test address")
        .phoneNumber("010-1234-5678")
        .role(Role.BUYER)
        .build();

    BuyerBalance buyerBalance = BuyerBalance.builder()
        .memberId(1L)
        .balance(0L).build();

    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
    when(buyerBalanceRepository.findByMemberId(anyLong())).thenReturn(Optional.of(buyerBalance));

    // when
    MemberInfoDto infoDto = buyerService.chargeBalance(1L, 10000L);

    // Then
    assertEquals(10000L, infoDto.getBalance());
    verify(memberRepository, times(1)).findById(1L);
    verify(buyerBalanceRepository, times(1)).findByMemberId(1L);
  }
}