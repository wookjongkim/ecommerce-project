package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.domain.model.BuyerBalance;
import com.example.ecommerceproject.domain.model.SellerRevenue;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface SellerRevenueRepository extends JpaRepository<SellerRevenue, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<SellerRevenue> findByMemberId(Long memberId);
}
