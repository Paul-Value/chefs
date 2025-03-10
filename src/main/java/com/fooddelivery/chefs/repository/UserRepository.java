package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
