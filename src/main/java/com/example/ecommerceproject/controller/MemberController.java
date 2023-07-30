package com.example.ecommerceproject.controller;


import com.example.ecommerceproject.domain.dto.LoginFormDto;
import com.example.ecommerceproject.domain.dto.SignUpFormDto;
import com.example.ecommerceproject.domain.dto.response.ApiResponse;
import com.example.ecommerceproject.service.impl.MemberService;
import com.example.ecommerceproject.util.ValidUtil;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/members")
  public ResponseEntity<ApiResponse> signUp(@Valid SignUpFormDto signUpFormDto,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return ValidUtil.extractErrorMessages(bindingResult);
    }

    StringBuilder sb = new StringBuilder();
    String name = memberService.signUp(signUpFormDto);

    sb.append(name);
    sb.append("고객님 회원 가입이 완료되었습니다! 즐거운 하루 되세요");

    return new ResponseEntity<>(new ApiResponse(200, sb.toString()), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@Valid LoginFormDto loginFormDto,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return ValidUtil.extractErrorMessages(bindingResult);
    }

    StringBuilder sb = new StringBuilder();
    String name = memberService.login(loginFormDto);

    sb.append(name);
    sb.append("고객님 로그인이 완료되었습니다! 즐거운 하루 되세요");

    return new ResponseEntity<>(new ApiResponse(200, sb.toString()), HttpStatus.OK);
  }
}
