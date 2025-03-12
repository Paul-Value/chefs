package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ChefRepository extends JpaRepository<Chef, Long> {
    List<Chef> findByIsWorkingTrue();

    Optional<Chef> findByAccessCode(String accessCode);

    @Query("SELECT c FROM Chef c LEFT JOIN FETCH c.foods WHERE c.chefId = :chefId")
    Optional<Chef> findByIdWithFoods(@Param("chefId") Long chefId);

    @Query("SELECT c FROM Chef c WHERE c.isWorking = true AND FUNCTION('calculate_distance', c.address.latitude, c.address.longitude, :lat, :lon) <= :radius")
    List<Chef> findNearbyChefs(@Param("lat") BigDecimal latitude, @Param("lon") BigDecimal longitude, @Param("radius") double radius);
}
