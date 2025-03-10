package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChefRepository extends JpaRepository<Chef, Long> {
}
