package com.example.ecommerceproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  SELLER_NOT_FOUND(400, "E0001", "존재하지 않는 판매자입니다."),
  UNAUTHORIZED_REQUEST(403, "E0002", "권한이 없는 요청입니다."),
  ITEM_NOT_MATCH(400, "E0003", "해당 판매자가 조회하고자 하는 상품이 존재하지 않습니다."),
  STOCK_NOT_FOUND(404, "E0004", "요청 하신 상품의 재고를 찾을 수 없습니다.");

  private final int status;
  private final String code;
  private final String message;
}
