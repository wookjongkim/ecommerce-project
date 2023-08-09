package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByEmailAndRole(String email, Role role);
  Optional<Member> findByEmail(String email);
}
