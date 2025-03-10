package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
