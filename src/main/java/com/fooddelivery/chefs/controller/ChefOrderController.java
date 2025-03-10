package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.model.dto.OrderStatusUpdateRequest;
import com.fooddelivery.chefs.service.OrderEventPublisher;
import com.fooddelivery.chefs.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chefs/orders")
@RequiredArgsConstructor
public class ChefOrderController {
    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<OrderEvent>> streamOrders() {
        return eventPublisher.getSink().asFlux()
                .map(event -> ServerSentEvent.builder(event)
                        .event("new_order")
                        .build());
    }

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
