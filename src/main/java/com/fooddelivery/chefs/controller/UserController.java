package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/personal-data")
    public ResponseEntity<String> updatePersonalData(
            @RequestHeader("X-Device-Id") String deviceId,
            @RequestBody UserUpdateRequest request
    ) {
        userService.updateUserData(deviceId, request);
        return ResponseEntity.ok("Personal data updated successfully");
    }
}
