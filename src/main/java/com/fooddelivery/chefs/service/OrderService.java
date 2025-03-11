package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.Chef;
import com.fooddelivery.chefs.model.Customer;
import com.fooddelivery.chefs.model.Order;
import com.fooddelivery.chefs.model.OrderEvent;
import com.fooddelivery.chefs.model.dto.OrderCreateRequest;
import com.fooddelivery.chefs.model.dto.OrderCreateResponse;
import com.fooddelivery.chefs.model.dto.OrderResponse;
import com.fooddelivery.chefs.model.enums.OrderStatus;
import com.fooddelivery.chefs.repository.ChefRepository;
import com.fooddelivery.chefs.repository.CustomerRepository;
import com.fooddelivery.chefs.repository.FoodRepository;
import com.fooddelivery.chefs.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ChefRepository chefRepository;
    private final FoodRepository foodRepository;
    private final CustomerRepository customerRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public OrderCreateResponse createOrder(String deviceId, OrderCreateRequest request) {
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (customer.getAddress() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Адрес не указан");
        }

        BigDecimal totalPrice = calculateTotalPrice(request.getItems());
        Order order = buildOrder(customer, request, totalPrice);
        orderRepository.save(order);

        eventPublisher.publish(new OrderEvent(order.getOrderId(), order.getChef().getChefId(), customer.getName()));
        return OrderCreateResponse.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .totalPrice(totalPrice)
                .build();
    }

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

    private Order buildOrder(Customer customer, OrderCreateRequest request, BigDecimal totalPrice) {
        return Order.builder()
                .customer(customer)
                .chef(findChefForOrder(request.getItems()))
                .itemsJson(convertItemsToJson(request.getItems()))
                .totalPrice(totalPrice)
                .status(OrderStatus.CREATED)
                .comment(request.getComment())
                .build();
    }

    public List<OrderResponse> getCurrentOrders(String deviceId) {
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return orderRepository.findByCustomerIdAndStatusIn(
                        customer.getCustomerId(),
                        List.of(OrderStatus.CREATED, OrderStatus.IN_COOKING, OrderStatus.DELIVERING)
                ).stream()
                .map(Order::toResponse)
                .toList();
    }
}
