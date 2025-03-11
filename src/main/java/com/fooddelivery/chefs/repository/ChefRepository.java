package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChefRepository extends JpaRepository<Chef, Long> {
    Optional<Chef> findByAccessCode(String accessCode);

    @Query("SELECT c FROM Chef c WHERE c.chef_Id = :chefId")
    Optional<Chef> findByIdWithFoods(@Param("chefId") Long chefId);
}
