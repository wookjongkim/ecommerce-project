package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import com.example.ecommerceproject.domain.model.BuyerBalance;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.BuyerBalanceRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import com.example.ecommerceproject.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {

  private final BuyerBalanceRepository buyerBalanceRepository;
  private final MemberRepository memberRepository;

  @Transactional
  @Override
  public MemberInfoDto chargeBalance(Long buyerId, Long chargeVal) {

    Member member = memberRepository.findById(buyerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.BUYER_NOT_FOUND));

    BuyerBalance buyerBalance = buyerBalanceRepository.findByMemberId(member.getId())
        .orElseThrow(() -> new BusinessException(ErrorCode.BALANCE_NOT_FOUND));

    buyerBalance.setBalance(buyerBalance.getBalance() + chargeVal);

    return MemberInfoDto.of(member, buyerBalance.getBalance());
  }
}
