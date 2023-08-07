package com.example.ecommerceproject.service;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.response.BuyerItemResponseDto;
import com.example.ecommerceproject.domain.dto.response.MemberInfoDto;
import com.example.ecommerceproject.domain.dto.request.OrderRequestDto;
import com.example.ecommerceproject.domain.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyerService {

  MemberInfoDto chargeBalance(Long buyerId, Long chargeVal);

  Page<BuyerItemResponseDto> lookupItems(String component, int minPrice, int maxPrice,
      String priceOrder, ItemSellStatus itemSellStatus, Category category, Pageable pageable);

  OrderResponseDto orderItems(Long buyerId, OrderRequestDto orderRequestDto);

  String cancelOrder(Long buyerId, Long orderId);

  OrderResponseDto lookupOrder(Long buyerId, Long orderId);
}
