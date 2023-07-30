package com.example.ecommerceproject.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.LoginFormDto;
import com.example.ecommerceproject.domain.dto.SignUpFormDto;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.BuyerBalanceRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import java.util.Optional;
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

  @Mock
  private BuyerBalanceRepository buyerBalanceRepository;

  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp(){
    passwordEncoder = new BCryptPasswordEncoder();
    memberService = new MemberService(passwordEncoder, memberRepository, buyerBalanceRepository);
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

  @Test
  @DisplayName("유효한 이메일과 비밀번호로 로그인 성공")
  void loginTest(){
    //Given
    LoginFormDto loginFormDto = LoginFormDto.builder()
        .email("test@example.com")
        .password("Test123!").build();

    Member member = Member.builder()
        .name("test")
        .email("test@example.com")
        .password(passwordEncoder.encode("Test123!"))
        .address("test address")
        .phoneNumber("010-1234-5678")
        .role(Role.BUYER)
        .build();

    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

    // when
    String logInMemberNm = memberService.login(loginFormDto);

    // Then
    assertEquals(member.getName(), logInMemberNm);
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 실패")
  void loginWithNonExistEmail(){
    // Given
    LoginFormDto loginFormDto = LoginFormDto.builder()
        .email("test@example.com")
        .password("Test123!").build();

    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // when
    BusinessException exception = assertThrows(BusinessException.class,
        () -> memberService.login(loginFormDto)
    );

    // then
    assertEquals(ErrorCode.LOGIN_EMAIL_INVALID, exception.getErrorCode());
  }

  @Test
  @DisplayName("잘못된 비밀번호로 로그인 실패")
  void loginWithInvalidPassword(){
    // Given
    LoginFormDto loginFormDto = LoginFormDto.builder()
        .email("test@example.com")
        .password("Test123!").build();

    Member member = Member.builder()
        .name("test")
        .email("test@example.com")
        .password(passwordEncoder.encode("UnMatch123!"))
        .address("test address")
        .phoneNumber("010-1234-5678")
        .role(Role.BUYER)
        .build();

    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

    // when
    BusinessException exception = assertThrows(BusinessException.class,
        () -> memberService.login(loginFormDto)
    );

    // then
    assertEquals(ErrorCode.LOGIN_PASSWORD_INVALID, exception.getErrorCode());
  }
}