package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.Customer;
import com.fooddelivery.chefs.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final CustomerRepository customerRepository;

    public void updateUserData(String deviceId, UserUpdateRequest request) {
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);
    }
}
