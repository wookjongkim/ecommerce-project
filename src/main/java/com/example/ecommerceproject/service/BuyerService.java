package com.example.ecommerceproject.service;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.BuyerItemResponseDto;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyerService {

  MemberInfoDto chargeBalance(Long buyerId, Long chargeVal);

  Page<BuyerItemResponseDto> lookupItems(String component, int minPrice, int maxPrice,
      String priceOrder, ItemSellStatus itemSellStatus, Category category, Pageable pageable);
}
