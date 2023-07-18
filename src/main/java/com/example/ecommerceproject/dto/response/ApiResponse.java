package com.example.ecommerceproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {

  private int status;
  private boolean success;
  private String message;
  private T data;

  // success response
  public static <T> ApiResponse<T> success(int status, String message, T data){
    return new ApiResponse<T>(status, true, message, data);
  }

  // error response
  public static <T> ApiResponse<T> error(int status, String message){
    return new ApiResponse<T>(status, false, message, null);
  }
}
