package com.example.ecommerceproject.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.SignUpFormDto;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.MemberRepository;
import org.hibernate.boot.model.source.internal.hbm.ManyToOnePropertySource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp(){
    passwordEncoder = new BCryptPasswordEncoder();
    memberService = new MemberService(passwordEncoder, memberRepository);
  }

  @Test
  @DisplayName("회원 가입 테스트")
  void signUpTest(){
    //Given
    SignUpFormDto signUpFormDto = SignUpFormDto.builder()
        .name("test")
        .email("test@example.com")
        .password("Test123!")
        .address("test address")
        .phoneNumber("010-1234-5678")
        .role(Role.SELLER)
        .build();

    when(memberRepository.existsByEmailAndRole(anyString(), any(Role.class))).thenReturn(false);

    // When
    String registeredMemberName = memberService.signUp(signUpFormDto);

    // Then
    verify(memberRepository).save(any(Member.class));
    assertEquals(signUpFormDto.getName(), registeredMemberName);
  }

  @Test
  @DisplayName("중복된 이메일로 회원가입 불가")
  void singUpWithDuplicateEmail(){
    //Given
    SignUpFormDto signUpFormDto = SignUpFormDto.builder()
        .name("test")
        .email("test@example.com")
        .password("Test123!")
        .address("test address")
        .phoneNumber("010-1234-5678")
        .role(Role.SELLER)
        .build();

    // When & Then
    when(memberRepository.existsByEmailAndRole(anyString(), any(Role.class))).thenReturn(true);

    BusinessException businessException = assertThrows(BusinessException.class,
        () -> memberService.signUp(signUpFormDto));
    assertEquals(ErrorCode.MEMBER_ALREADY_EXIST.getMessage(), businessException.getMessage());
  }
}