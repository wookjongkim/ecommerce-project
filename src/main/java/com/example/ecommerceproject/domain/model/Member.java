package com.example.ecommerceproject.domain.model;


import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.SignUpFormDto;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

  @Id
  @Column(name = "member_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String name;

  private String password;

  private String address;

  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  private Role role;

  public static Member of(SignUpFormDto signUpFormDto) {
    return Member.builder()
        .email(signUpFormDto.getEmail())
        .name(signUpFormDto.getName())
        .password(signUpFormDto.getPassword())
        .address(signUpFormDto.getAddress())
        .phoneNumber(signUpFormDto.getPhoneNumber())
        .role(signUpFormDto.getRole())
        .build();
  }
}
