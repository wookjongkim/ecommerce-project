package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.domain.model.BuyerBalance;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

public interface BuyerBalanceRepository extends JpaRepository<BuyerBalance, Long> {

  // 비관적 락(쓰기 동작을 하지 않아도 쓰기락이 걸림). 이는 트랜잭션이 커밋되거나 롤백될떄까지 유지됨.
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value="10000")})
  Optional<BuyerBalance> findByMemberId(Long memberId);
}
