package com.example.ecommerceproject.repository;

import com.example.ecommerceproject.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
