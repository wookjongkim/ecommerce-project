package com.example.ecommerceproject.service;

import com.example.ecommerceproject.domain.dto.MemberInfoDto;

public interface BuyerService {
  MemberInfoDto chargeBalance(Long buyerId, Long chargeVal);
}
