package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.Chef;
import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.enums.OrderStatus;
import com.fooddelivery.chefs.repository.ChefRepository;
import com.fooddelivery.chefs.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ChefRepository chefRepository;

    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<OrderResponse> getChefOrderHistory(String accessCode) {
        Chef chef = chefRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return orderRepository.findByChefIdAndStatusIn(
                        chef.getChefId(),
                        List.of(OrderStatus.COMPLETED, OrderStatus.CANCELED)
                ).stream()
                .map(Order::toResponse)
                .toList();
    }
}
