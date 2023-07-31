package com.example.ecommerceproject.controller;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.BuyerItemResponseDto;
import com.example.ecommerceproject.domain.dto.ChargeDto;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import com.example.ecommerceproject.domain.dto.response.SuccessResponse;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/buyers")
@RestController
@RequiredArgsConstructor
public class BuyerController {

  private final BuyerService buyerService;

  @PostMapping("/{buyerId}/balance")
  public ResponseEntity<SuccessResponse> chargeBalance(
      @PathVariable Long buyerId, @RequestBody ChargeDto chargeDto
  ){

    MemberInfoDto infoDto = buyerService.chargeBalance(buyerId, chargeDto.getChargeVal());

    return new ResponseEntity<>(new SuccessResponse(200, "잔액 충전이 완료되었습니다.", infoDto), HttpStatus.OK);
  }

  @GetMapping("/items")
  public ResponseEntity<SuccessResponse> lookupItems(
      @RequestParam(required = false, defaultValue = "") String component, // 검색어
      @RequestParam(required = false, defaultValue = "0") Integer minPrice,
      @RequestParam(required = false, defaultValue = "2147483647") Integer maxPrice,
      @RequestParam(defaultValue = "asc") String priceOrder,
      @RequestParam(required = false) ItemSellStatus saleStatus,
      @RequestParam(required = false) Category category,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size
  ){
    Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.fromString(priceOrder), "price"));

    if(saleStatus == null){
      saleStatus = ItemSellStatus.SELL;
    }

    Page<BuyerItemResponseDto> items = buyerService.lookupItems(component, minPrice,
        maxPrice, priceOrder, saleStatus, category, pageable);

    return new ResponseEntity<>(new SuccessResponse(200, "상품 조회가 완료되었습니다.", items),
        HttpStatus.OK);
  }
}
