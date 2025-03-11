package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.model.dto.OrderPriceResponse;
import com.fooddelivery.chefs.model.dto.PriceCalculationRequest;
import com.fooddelivery.chefs.service.OrderEventPublisher;
import com.fooddelivery.chefs.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);

        // Отправка события
        OrderEvent event = new OrderEvent();
        event.setOrderId(order.getOrderId());
        event.setChefId(order.getChef().getChefId());
        event.setCustomerName(order.getCustomer().getName());
        event.setCreatedAt(order.getCreatedAt().toInstant());
        eventPublisher.publish(event);

        return ResponseEntity.ok(order.toResponse());
    }
}
