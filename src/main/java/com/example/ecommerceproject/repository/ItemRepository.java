package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.domain.model.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
  Optional<Item> findByIdAndSellerId(long itemId, long sellerId);
}
