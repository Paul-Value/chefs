package com.fooddelivery.chefs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.chefs.model.*;
import com.fooddelivery.chefs.model.dto.*;
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
import java.time.Instant;
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

        if (customer.getAddressId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Адрес не указан");
        }

        BigDecimal totalPrice = calculateTotalPrice(request.getItems());
        Order order = buildOrder(customer, request, totalPrice);
        orderRepository.save(order);

        eventPublisher.publish(new OrderEvent(order.getOrderId(), order.getChef().getUserId(), customer.getName(), Instant.now()));
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
                        chef.getUserId(),
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

    private String convertItemsToJson(List<OrderItemRequest> items) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка преобразования данных");
        }
    }

    private Chef findChefForOrder(List<OrderItemRequest> items) {
        // Предполагаем, что все блюда в заказе принадлежат одному повару
        Long chefId = items.stream()
                .map(item -> foodRepository.findById(item.getFoodId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                        .map(Food::getChefId)
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        return chefRepository.findById(chefId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Повар не найден"));
    }

    public List<OrderResponse> getCurrentOrders(String deviceId) {
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return orderRepository.findByCustomerAndStatusIn(
                        customer,
                        List.of(OrderStatus.CREATED, OrderStatus.IN_COOKING, OrderStatus.DELIVERING)
                ).stream()
                .map(Order::toResponse)
                .toList();
    }

    public OrderResponse handleOrderResponse(Long orderId, String accessCode, String action, String rejectComment) {
        Chef chef = chefRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if ("accept".equals(action)) {
            order.setStatus(OrderStatus.IN_COOKING);
            order.setChef(chef);
        } else if ("reject".equals(action)) {
            order.setStatus(OrderStatus.CANCELED);
            order.setRejectComment(rejectComment);
        }

        orderRepository.save(order);
        return convertToOrderResponse(order);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .chefName(order.getChef().getName())
                .chefPhone(order.getChef().getPhone())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalPrice(order.getTotalPrice())
                .items(order.getItemsJson().stream()
                        .map(item -> new OrderItemResponse(item.getFoodId(), item.getQuantity()))
                        .toList())
                .build();
    }


    private BigDecimal calculateTotalPrice(List<OrderItemRequest> items) {
        return items.stream()
                .map(item -> {
                    Food food = foodRepository.findById(item.getFoodId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Блюдо не найдено"));
                    return food.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
