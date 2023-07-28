package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.model.Item;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
  Optional<Item> findByIdAndSellerId(long itemId, long sellerId);

  List<Item> findAllBySaleStatusInAndUpdatedAtBefore(List<ItemSellStatus> statuses, LocalDateTime time);
}
