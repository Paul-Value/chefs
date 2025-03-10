package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.dto.UserUpdateRequest;
import com.fooddelivery.chefs.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/personal-data")
    public ResponseEntity<String> updateUserData(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.updateUserData(deviceId, request);
        return ResponseEntity.ok("Данные обновлены");
    }
}
