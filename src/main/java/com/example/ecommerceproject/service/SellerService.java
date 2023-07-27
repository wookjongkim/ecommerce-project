package com.example.ecommerceproject.service;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.ItemDetailDto;
import com.example.ecommerceproject.domain.dto.ItemFormRequestDto;
import com.example.ecommerceproject.domain.dto.ItemUpdateDto;
import com.example.ecommerceproject.domain.dto.SellerItemResponseDto;
import com.example.ecommerceproject.domain.model.Item;
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
}

