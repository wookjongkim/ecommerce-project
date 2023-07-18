package com.example.ecommerceproject.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse<T> extends ApiResponse{
  private T data;

  public SuccessResponse(int status, String message, T data){
    super(status, message);
    this.data = data;
  }
}
