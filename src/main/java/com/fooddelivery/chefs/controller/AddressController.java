package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.dto.AddressSearchRequest;
import com.fooddelivery.chefs.model.dto.AddressSearchResult;
import com.fooddelivery.chefs.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final GeocodingService geocodingService;

    @PostMapping("/search")
    public List<AddressSearchResult> searchAddress(
            @RequestHeader("X-Device-Id") String deviceId,
            @RequestBody AddressSearchRequest request
    ) {
        return geocodingService.searchAddress(request.getQuery());
    }
}
