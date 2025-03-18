package com.fooddelivery.chefs.controller;

import com.fooddelivery.chefs.model.Chef;
import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.model.dto.ChefDetailsResponse;
import com.fooddelivery.chefs.model.dto.ChefResponse;
import com.fooddelivery.chefs.model.dto.ChefValidationResponse;
import com.fooddelivery.chefs.repository.ChefRepository;
import com.fooddelivery.chefs.service.ChefService;
import com.fooddelivery.chefs.service.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chefs")
@RequiredArgsConstructor
public class ChefController {
    private final OrderEventPublisher eventPublisher;
    private final ChefService chefService;
    private final ChefRepository chefRepository;

    @GetMapping("/{chefId}")
    public ResponseEntity<ChefDetailsResponse> getChefDetails(@PathVariable Long chefId) {
        ChefDetailsResponse response = chefService.getChefDetails(chefId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-access")
    public ResponseEntity<ChefValidationResponse> validateAccess(
            @RequestHeader("X-Access-Code") String accessCode
    ) {
        Chef chef = chefRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return ResponseEntity.ok(ChefValidationResponse.builder()
                .chefId(chef.getUserId())
                .message("Код действителен")
                .build());
    }

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
