package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
