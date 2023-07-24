package com.example.ecommerceproject.service;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.ItemFormRequestDto;
import com.example.ecommerceproject.domain.model.Item;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerService {

  String addItem(ItemFormRequestDto itemFormRequestDto);

  Page<Item> getItems(Long sellerId, LocalDate startDate, LocalDate endDate,
      int minPrice, int maxPrice, ItemSellStatus itemSellStatus,
      String quantityOrder, Pageable pageable);
}

