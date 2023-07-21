package com.example.ecommerceproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  SELLER_NOT_FOUND(400, "E0001", "존재하지 않는 판매자입니다."),
  UNAUTHORIZED_REQUEST(403, "E0002", "권한이 없는 요청입니다.");

  private final int status;
  private final String code;
  private final String message;
}
