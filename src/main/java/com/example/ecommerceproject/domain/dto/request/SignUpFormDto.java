package com.example.ecommerceproject.domain.dto.request;

import com.example.ecommerceproject.constant.Role;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
public class SignUpFormDto {

  @Size(max = 6, message = "이름은 6자 미만이여야 합니다.")
  @NotBlank(message = "이름은 필수 항목입니다.")
  private String name;

  @Email(message = "이메일 형식이 잘못되었습니다.")
  @NotBlank(message = "이메일은 필수 항목입니다.")
  private String email;

  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,20}$",
      message = "비밀번호는 8~20자, 영문 대소문자, 숫자, 특수문자(!,@,#,$,%,^,&,+,=)가 최소 1개 포함되어야 합니다.")
  @NotBlank(message = "비밀번호는 필수 항목입니다.")
  private String password;

  @NotBlank(message = "주소는 필수 항목입니다.")
  private String address;

  @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
      message = "전화번호는 010-0000-0000 형식이어야 합니다.")
  @NotBlank(message = "전화번호는 필수 항목입니다.")
  private String phoneNumber;

  @NotNull(message = "역할을 필수 항목입니다. 판매자 : Seller, 구매자 : Buyer")
  private Role role;
}
