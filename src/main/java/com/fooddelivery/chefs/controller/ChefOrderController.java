package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chefs/orders")
@RequiredArgsConstructor
public class ChefOrderController {
    private final OrderService orderService;

    @PostMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest request
    ) {
        Order order = orderService.updateStatus(orderId, request.getStatus());
        return ResponseEntity.ok(order.toResponse());
    }

    @GetMapping("/history")
    public List<OrderResponse> getOrderHistory(
            @RequestHeader("X-Access-Code") String accessCode
    ) {
        return orderService.getChefOrderHistory(accessCode);
    }
}
