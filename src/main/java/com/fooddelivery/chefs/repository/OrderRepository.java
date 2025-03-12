package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Chef;
import com.fooddelivery.chefs.model.Customer;
import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByChefAndStatusIn(Chef chef, Collection<OrderStatus> status);

    List<Order> findByCustomerAndStatusIn(Customer customer, List<OrderStatus> statuses);
}
