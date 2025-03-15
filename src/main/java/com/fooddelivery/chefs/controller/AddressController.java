package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.dto.AddressSaveRequest;
import com.fooddelivery.chefs.model.dto.AddressSearchRequest;
import com.fooddelivery.chefs.model.dto.GeocodingResponse;
import com.fooddelivery.chefs.service.AddressService;
import com.fooddelivery.chefs.service.GeocodingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final GeocodingService geocodingService;
    private final AddressService addressService;

    @PostMapping("/search")
    public ResponseEntity<List<GeocodingResponse>> searchAddress(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody AddressSearchRequest request
    ) {
        List<GeocodingResponse> results = geocodingService.searchAddress(request.getQuery());
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<?> saveAddress(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody AddressSaveRequest request
    ) {
        addressService.saveAddress(deviceId, request);
        return ResponseEntity.ok(Map.of("message", "Address updated successfully"));
    }
}
