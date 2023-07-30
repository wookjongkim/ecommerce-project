package com.example.ecommerceproject.domain.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginFormDto {

  @Email(message = "이메일 형식이 잘못되었습니다.")
  @NotBlank(message = "이메일은 필수 항목입니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수 항목입니다.")
  private String password;
}
