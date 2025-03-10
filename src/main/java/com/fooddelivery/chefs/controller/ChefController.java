package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.service.ChefService;
import com.fooddelivery.chefs.service.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/chefs")
@RequiredArgsConstructor
public class ChefController {
    private final OrderEventPublisher eventPublisher;
    private final ChefService chefService;

    @GetMapping(path = "/orders/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<OrderEvent>> streamOrders() {
        return eventPublisher.getSink().asFlux()
                .map(event -> ServerSentEvent.builder(event)
                        .event("new_order")
                        .build())
                .doOnCancel(() -> log.info("SSE connection closed"));
    }

    @GetMapping
    public List<ChefResponse> getNearbyChefs(
            @RequestHeader("X-Device-Id") String deviceId,
            @RequestParam(defaultValue = "5") double radiusKm
    ) {
        return chefService.findNearbyChefs(deviceId, radiusKm);
    }
}
