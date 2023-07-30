package com.example.ecommerceproject.controller;

import com.example.ecommerceproject.domain.dto.ChargeDto;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import com.example.ecommerceproject.domain.dto.response.SuccessResponse;
import com.example.ecommerceproject.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
