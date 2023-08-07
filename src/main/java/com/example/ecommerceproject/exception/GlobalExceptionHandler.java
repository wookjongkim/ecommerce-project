package com.example.ecommerceproject.exception;

import com.example.ecommerceproject.domain.dto.response.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException businessException){

    ErrorCode errorCode = businessException.getErrorCode();

    int status = errorCode.getStatus();
    String message = errorCode.getMessage();
    String code = errorCode.getCode();

    return new ResponseEntity<>(new ErrorResponse(status, code, message), HttpStatus.valueOf(status));
  }
}
