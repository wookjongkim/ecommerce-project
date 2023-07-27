package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.domain.model.Stock;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

  Optional<Stock> findById(Long id);

  // 이경우 JPA에 내장된 키워드를 사용한 메서드의 이름이 아니기에, JPA는 이 메서드의 기능을 알 수 없고, 해당 쿼리 생성 불가
  // 이 경우 @Query 어노테이션을 사용해서 메서드의 쿼리를 직접 제공해야함.
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select s from Stock s where s.id = :id")
  Optional<Stock> findByIdForUpdate(@Param("id") Long stockId);

}
