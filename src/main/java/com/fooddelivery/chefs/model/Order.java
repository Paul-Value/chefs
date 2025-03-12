package com.fooddelivery.chefs.model;

import com.fooddelivery.chefs.model.dto.OrderItemResponse;
import com.fooddelivery.chefs.model.dto.OrderResponse;
import com.fooddelivery.chefs.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "chef_id", nullable = false)
    private Chef chef;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String comment;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "items_json", columnDefinition = "jsonb", nullable = false)
    private List<OrderItem> itemsJson;

    public OrderResponse toResponse() {
        return OrderResponse.builder()
                .orderId(this.orderId)
                .chefName(this.chef.getName())
                .chefPhone(this.chef.getPhone())
                .status(this.status)
                .createdAt(this.createdAt)
                .totalPrice(this.totalPrice)
                .items(this.convertItemsToResponse())
                .build();
    }

    @Column(name = "reject_comment")
    private String rejectComment;

    private List<OrderItemResponse> convertItemsToResponse() {
        // Логика преобразования items_json в List<OrderItemResponse>
    }
}
