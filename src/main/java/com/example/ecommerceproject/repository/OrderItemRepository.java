package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.domain.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
