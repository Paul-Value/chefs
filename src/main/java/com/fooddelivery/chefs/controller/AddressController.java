package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.dto.AddressSearchRequest;
import com.fooddelivery.chefs.model.dto.AddressSearchResult;
import com.fooddelivery.chefs.model.dto.GeocodingResponse;
import com.fooddelivery.chefs.service.GeocodingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final GeocodingService geocodingService;

    @PostMapping("/search")
    public ResponseEntity<List<GeocodingResponse>> searchAddress(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody AddressSearchRequest request
    ) {
        List<GeocodingResponse> results = geocodingService.searchAddress(request.getQuery());
        return ResponseEntity.ok(results);
    }
}
