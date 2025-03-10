package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.Address;
import com.fooddelivery.chefs.model.Chef;
import com.fooddelivery.chefs.model.Customer;
import com.fooddelivery.chefs.repository.ChefRepository;
import com.fooddelivery.chefs.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChefService {
    private final CustomerRepository customerRepository;
    private final ChefRepository chefRepository;

    public List<ChefResponse> findNearbyChefs(String deviceId, double radiusKm) {
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        List<Chef> chefs = chefRepository.findByIsWorkingTrue();
        return chefs.stream()
                .filter(chef -> calculateDistance(customer.getAddress(), chef.getAddress()) <= radiusKm)
                .map(Chef::toResponse)
                .toList();
    }

    private double calculateDistance(Address customerAddr, Address chefAddr) {
        // Формула гаверсинусов
        return ...;
    }
}

