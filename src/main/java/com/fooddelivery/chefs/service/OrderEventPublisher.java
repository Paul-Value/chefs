package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.model.enums.OrderStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class OrderEventPublisher {
    // Sinks.Many для рассылки событий
    private final Sinks.Many<OrderEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    /*public void publishStatusUpdate(Long orderId, OrderStatus newStatus) {
        OrderEvent event = new OrderEvent(orderId, newStatus);
        sink.tryEmitNext(event);
    }*/

    public void publish(OrderEvent event) {
        sink.tryEmitNext(event);
    }

    public Sinks.Many<OrderEvent> getSink() {
        return sink;
    }
}
