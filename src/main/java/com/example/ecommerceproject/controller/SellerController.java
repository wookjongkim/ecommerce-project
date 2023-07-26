package com.example.ecommerceproject.controller;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.ItemDetailDto;
import com.example.ecommerceproject.domain.dto.ItemFormRequestDto;
import com.example.ecommerceproject.domain.dto.ItemUpdateDto;
import com.example.ecommerceproject.domain.dto.SellerItemResponseDto;
import com.example.ecommerceproject.domain.dto.response.ApiResponse;
import com.example.ecommerceproject.domain.dto.response.SuccessResponse;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.service.SellerService;
import com.example.ecommerceproject.util.ValidUtil;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/sellers")
@RestController
@RequiredArgsConstructor
public class SellerController {

  private final SellerService sellerService;

  @PostMapping("/{sellerId}/item")
  public ResponseEntity<ApiResponse> itemNew(@PathVariable Long sellerId,
      @Valid ItemFormRequestDto itemFormRequestDto, BindingResult bindingResult) {

    // 입력값 검증
    if (bindingResult.hasErrors()) {
      ValidUtil.extractErrorMessages(bindingResult);
    }

    String msg = sellerService.addItem(sellerId, itemFormRequestDto);

    return new ResponseEntity<>(new ApiResponse(200, msg), HttpStatus.OK);
  }

  @GetMapping("/{sellerId}/items")
  public ResponseEntity<SuccessResponse> getItems(
      @PathVariable Long sellerId,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam(required = false, defaultValue = "0") Integer minPrice,
      @RequestParam(required = false, defaultValue = "2147483647") Integer maxPrice,
      @RequestParam(required = false) ItemSellStatus saleStatus,
      @RequestParam(required = false, defaultValue = "asc") String quantityOrder,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size,
      @RequestParam(defaultValue = "id") String sort
  ) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

    // 입력하지 않은 값들에 대해 초기값 세팅
    if (saleStatus == null) {
      saleStatus = ItemSellStatus.SELL;
    }
    if (startDate == null) {
      startDate = LocalDate.of(1970, 1, 1);
    }
    if (endDate == null) {
      endDate = LocalDate.of(2023, 12, 31);
    }

    Page<Item> items = sellerService.getItems(sellerId, startDate, endDate, minPrice, maxPrice,
        saleStatus, quantityOrder, pageable);

    Page<SellerItemResponseDto> itemList = items.map(SellerItemResponseDto::of);

    return new ResponseEntity<>(new SuccessResponse(200, "상품 조회가 완료되었습니다.", itemList),
        HttpStatus.OK);
  }

  @GetMapping("/{sellerId}/items/{itemId}")
  public ResponseEntity<SuccessResponse> getItem(@PathVariable Long sellerId,
      @PathVariable Long itemId) {
    ItemDetailDto itemDetail = sellerService.getItem(sellerId, itemId);

    return new ResponseEntity<>(new SuccessResponse(200, "상품 상세 정보 조회가 완료되었습니다.", itemDetail),
        HttpStatus.OK);
  }

  @PutMapping("/{sellerId}/items/{itemId}")
  public ResponseEntity<ApiResponse> editItem(@PathVariable Long sellerId,
      @PathVariable Long itemId, @Valid ItemUpdateDto itemUpdateDto, BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      ValidUtil.extractErrorMessages(bindingResult);
    }

    ItemUpdateDto updateDto = sellerService.editItem(sellerId, itemId, itemUpdateDto);

    return new ResponseEntity<>(new SuccessResponse<>(200, "상품 수정이 완료되었습니다.", updateDto), HttpStatus.OK);
  }
}
