package com.example.ecommerceproject.util;

import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.response.ApiResponse;
import com.example.ecommerceproject.domain.model.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public class ValidUtil {

  // 입력값에 대한 Valid 진행 시, 어떤 부분이 잘못되었는지 Customer에게 전달
  public static ResponseEntity<ApiResponse> extractErrorMessages(BindingResult bindingResult) {
    StringBuilder sb = new StringBuilder();

    bindingResult.getFieldErrors().forEach(error -> {
      sb.append(error.getField() + ": " + error.getDefaultMessage() + "\n");
    });

    ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST.value(), sb.toString());

    return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
  }

  public static boolean isSeller(Member member) {
    return Role.SELLER.equals(member.getRole());
  }

  public static boolean isBuyer(Member member){
    return Role.BUYER.equals(member.getRole());
  }
}
