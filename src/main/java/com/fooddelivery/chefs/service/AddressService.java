package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.Address;
import com.fooddelivery.chefs.model.Customer;
import com.fooddelivery.chefs.model.dto.AddressSaveRequest;
import com.fooddelivery.chefs.model.dto.GeocodingResponse;
import com.fooddelivery.chefs.repository.AddressRepository;
import com.fooddelivery.chefs.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final RedisCacheService redisCacheService;

    @Transactional
    public void saveAddress(String deviceId, AddressSaveRequest request) {
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        List<GeocodingResponse> cachedResults = redisCacheService.getCachedAddress(request.getPlaceId());
        if (cachedResults.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный place_id");
        }

        GeocodingResponse geocodingData = cachedResults.get(0);
        Address address = Address.builder()
                .customer(customer)
                .formattedAddress(geocodingData.getFormattedAddress())
                .latitude(geocodingData.getLatitude())
                .longitude(geocodingData.getLongitude())
                .entrance(request.getEntrance())
                .floor(request.getFloor())
                .apartment(request.getApartment())
                .build();

        addressRepository.save(address);
    }
}
