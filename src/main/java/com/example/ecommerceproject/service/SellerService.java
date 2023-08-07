package com.example.ecommerceproject.service;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.response.ItemDetailDto;
import com.example.ecommerceproject.domain.dto.request.ItemFormRequestDto;
import com.example.ecommerceproject.domain.dto.response.ItemUpdateDto;
import com.example.ecommerceproject.domain.dto.response.SellerItemResponseDto;
import com.example.ecommerceproject.domain.dto.request.WithdrawDto;
import com.example.ecommerceproject.domain.dto.response.WithdrawResponseDto;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerService {

  String addItem(Long sellerId, ItemFormRequestDto itemFormRequestDto);

  Page<SellerItemResponseDto> getItems(Long sellerId, LocalDate startDate, LocalDate endDate,
      int minPrice, int maxPrice, ItemSellStatus itemSellStatus,
      String quantityOrder, Pageable pageable);

  ItemDetailDto getItem(Long sellerId, Long itemId);

  ItemUpdateDto editItem(Long sellerId, Long itemId, ItemUpdateDto itemUpdateDto);

  SellerItemResponseDto addStock(Long sellerId, Long itemId, int addNum);

  WithdrawResponseDto withdrawBalance(Long sellerId, WithdrawDto withdrawDto);
}

