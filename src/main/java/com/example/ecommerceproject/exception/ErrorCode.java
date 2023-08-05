package com.example.ecommerceproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  SELLER_NOT_FOUND(400, "E0001", "존재하지 않는 판매자입니다."),
  UNAUTHORIZED_REQUEST(403, "E0002", "권한이 없는 요청입니다."),
  ITEM_NOT_MATCH(400, "E0003", "해당 판매자가 조회하고자 하는 상품이 존재하지 않습니다."),
  STOCK_NOT_FOUND(500, "E0004", "요청 하신 상품의 재고를 찾을 수 없습니다."),
  MEMBER_ALREADY_EXIST(400, "E0005", "이미 존재하는 이메일입니다. 다른 이메일을 선택해주세요."),
  LOGIN_EMAIL_INVALID(400, "E0006", "로그인에 입력한 이메일이 올바르지 않습니다"),
  LOGIN_PASSWORD_INVALID(400, "E0007", "로그인에 입력한 비밀번호가 올바르지 않습니다."),
  BUYER_NOT_FOUND(400, "E0008", "존재하지 않는 구매자입니다."),
  BALANCE_NOT_FOUND(500, "E0009", "판매자의 잔액 정보가 존재하지 않습니다."),

  UNORDERABLE_ITEM_INCLUDED(400, "E0010", "SELL_STOPPED, SOLD_OUT 상태의 상품이 주문에 포함되어 있습니다."),

  ITEM_NOT_EXIST(400, "E0011", "해당 아이템이 존재하지 않습니다."),
  OUT_OF_STOCK_ITEM_INCLUDED(400,"E0012","주문을 하기 위한 해당 상품의 재고가 부족합니다."),
  BUYER_BALANCE_NOT_FOUND(500, "E0013", "해당 사용자의 잔액 정보가 조회되지 않습니다."),

  INSUFFICIENT_BALANCE(400, "E0014", "주문을 진행하기에 계좌 잔고가 부족합니다."),
  ORDER_NOT_FOUND(400, "E0015", "존재하지 않는 주문 정보입니다."),
  ORDER_ALREADY_CANCELED(400, "E0016", "이미 취소된 주문 입니다."),
  SELLER_REVENUE_NOT_FOUND(500, "E0017", "판매자의 수익 계좌가 존재하지 않습니다.");

  private final int status;
  private final String code;
  private final String message;
}
