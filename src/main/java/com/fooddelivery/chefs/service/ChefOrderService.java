package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.enums.OrderStatus;
import com.fooddelivery.chefs.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ChefOrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!order.getChef().getIsWorking()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Повар не работает");
        }

        order.setStatus(status);
        orderRepository.save(order);
    }
}
