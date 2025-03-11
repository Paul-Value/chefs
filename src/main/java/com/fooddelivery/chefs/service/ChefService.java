package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.Address;
import com.fooddelivery.chefs.model.Chef;
import com.fooddelivery.chefs.model.Customer;
import com.fooddelivery.chefs.model.Food;
import com.fooddelivery.chefs.model.dto.ChefDetailsResponse;
import com.fooddelivery.chefs.model.dto.ChefResponse;
import com.fooddelivery.chefs.model.dto.FoodDetailsResponse;
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

    public ChefDetailsResponse getChefDetails(Long chefId) {
        Chef chef = chefRepository.findByIdWithFoods(chefId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToDetailsResponse(chef);
    }

    private ChefDetailsResponse convertToDetailsResponse(Chef chef) {
        return ChefDetailsResponse.builder()
                .chefId(chef.getChefId())
                .photoUrl(chef.getPhotoUrl())
                .description(chef.getDescription())
                .phone(chef.getPhone())
                .isWorking(chef.getIsWorking())
                .foods(chef.getFoods().stream()
                        .map(this::convertToFoodDetails)
                        .toList())
                .build();
    }

    private FoodDetailsResponse convertToFoodDetails(Food food) {
        return FoodDetailsResponse.builder()
                .foodId(food.getFoodId())
                .name(food.getName())
                .description(food.getDescription())
                .ingredients(food.getIngredients())
                .price(food.getPrice())
                .photoUrl(food.getPhotoUrl())
                .build();
    }

    public List<ChefResponse> getNearbyChefs(String deviceId, double radiusKm) {
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return chefRepository.findByIsWorkingTrue().stream()
                .filter(chef -> calculateDistance(customer.getAddress(), chef.getAddress()) <= radiusKm)
                .map(this::convertToResponse)
                .toList();
    }

    private ChefResponse convertToResponse(Chef chef) {
        return ChefResponse.builder()
                .chefId(chef.getChefId())
                .photoUrl(chef.getPhotoUrl())
                .description(chef.getDescription())
                .phone(chef.getPhone())
                .isWorking(chef.getIsWorking())
                .foods(chef.getFoods().stream()
                        .map(this::convertFoodToResponse)
                        .toList())
                .build();
    }

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
        double lat1 = Math.toRadians(customerAddr.getLatitude().doubleValue());
        double lon1 = Math.toRadians(customerAddr.getLongitude().doubleValue());
        double lat2 = Math.toRadians(chefAddr.getLatitude().doubleValue());
        double lon2 = Math.toRadians(chefAddr.getLongitude().doubleValue());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // Радиус Земли в км
    }
}

