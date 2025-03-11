package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.model.dto.OrderPriceResponse;
import com.fooddelivery.chefs.model.dto.OrderRequest;
import com.fooddelivery.chefs.model.dto.OrderResponse;
import com.fooddelivery.chefs.model.dto.PriceCalculationRequest;
import com.fooddelivery.chefs.service.OrderEventPublisher;
import com.fooddelivery.chefs.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;

    @PostMapping("/price")
    public ResponseEntity<OrderPriceResponse> calculatePrice(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody PriceCalculationRequest request
    ) {
        BigDecimal totalPrice = orderService.calculateTotalPrice(request.getItems());
        return ResponseEntity.ok(OrderPriceResponse.builder().totalPrice(totalPrice).build());
    }

    @GetMapping("/current")
    public ResponseEntity<List<OrderResponse>> getCurrentOrders(
            @RequestHeader("X-Device-Id") String deviceId
    ) {
        List<OrderResponse> orders = orderService.getCurrentOrders(deviceId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-Device-Id") String deviceId,
            @Valid @RequestBody OrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(deviceId, request);
        return ResponseEntity.ok(response);
    }
}
