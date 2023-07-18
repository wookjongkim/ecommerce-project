package com.example.ecommerceproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  TEMP_TEMP(400, "e0001", "테스트 에러");

  private final int status;
  private final String code;
  private final String message;
}
