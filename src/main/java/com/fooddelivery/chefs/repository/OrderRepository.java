package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
