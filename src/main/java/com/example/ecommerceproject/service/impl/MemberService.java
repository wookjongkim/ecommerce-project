package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.domain.dto.SignUpFormDto;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final PasswordEncoder passwordEncoder;

  private final MemberRepository memberRepository;

  public String signUp(SignUpFormDto signUpFormDto){
    // 입력한 이메일에 해당하는 유저가 이미 존재할 시 Exception 발생
    if(memberRepository.existsByEmailAndRole(signUpFormDto.getEmail(), signUpFormDto.getRole())){
      throw new BusinessException(ErrorCode.MEMBER_ALREADY_EXIST);
    }

    // 존재하지 않는다면 사용자가 입력한 비밀번호를 암호화한뒤에 DB에 저장
    Member member = Member.of(signUpFormDto);
    member.setPassword(passwordEncoder.encode(member.getPassword()));

    memberRepository.save(member);
    return member.getName();
  }
}
