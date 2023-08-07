package com.example.ecommerceproject.domain.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse{

  private int status;
  private String message;
}
