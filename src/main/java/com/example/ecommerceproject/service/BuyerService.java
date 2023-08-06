package com.example.ecommerceproject.service;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.BuyerItemResponseDto;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import com.example.ecommerceproject.domain.dto.OrderRequestDto;
import com.example.ecommerceproject.domain.dto.OrderResponseDto;
import java.util.List;
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
