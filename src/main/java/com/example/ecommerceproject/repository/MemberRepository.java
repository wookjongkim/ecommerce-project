package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByEmailAndRole(String email, Role role);
}
