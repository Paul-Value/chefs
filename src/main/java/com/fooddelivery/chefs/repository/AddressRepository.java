package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
