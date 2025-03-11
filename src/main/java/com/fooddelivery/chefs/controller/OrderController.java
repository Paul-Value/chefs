package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.service.OrderEventPublisher;
import com.fooddelivery.chefs.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;

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
