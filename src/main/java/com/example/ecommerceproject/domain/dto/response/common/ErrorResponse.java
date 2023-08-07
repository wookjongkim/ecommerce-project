package com.example.ecommerceproject.domain.dto.response.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends ApiResponse{

  private String errorCode;

  public ErrorResponse(int status, String errorCode, String message){
    super(status, message);
    this.errorCode = errorCode;
  }
}
