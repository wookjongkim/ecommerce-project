package com.example.ecommerceproject.controller;

import com.example.ecommerceproject.domain.dto.ItemFormDto;
import com.example.ecommerceproject.domain.dto.response.ApiResponse;
import com.example.ecommerceproject.service.ItemService;
import com.example.ecommerceproject.util.ValidUtil;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/sellers")
@RestController
@RequiredArgsConstructor
public class SellerController {

  private final ItemService itemService;

  @PostMapping("/item/new")
  public ResponseEntity<ApiResponse> itemNew(@Valid ItemFormDto itemFormDto,
      BindingResult bindingResult) {

    // 입력값 검증
    if (bindingResult.hasErrors()) {
      ValidUtil.extractErrorMessages(bindingResult);
    }

    String msg = itemService.addItem(itemFormDto);

    return new ResponseEntity<>(new ApiResponse(200, msg), HttpStatus.OK);
  }
}
