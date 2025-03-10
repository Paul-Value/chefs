package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChefRepository extends JpaRepository<Chef, Long> {
    Optional<Chef> findByAccessCode(String accessCode);
}
