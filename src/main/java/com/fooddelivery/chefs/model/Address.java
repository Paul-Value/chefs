package com.fooddelivery.chefs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "address")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(precision = 12, scale = 9, nullable = false)
    private BigDecimal longitude;

    @Column(precision = 12, scale = 9, nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private String formattedAddress;

    private String entrance;
    private String floor;
    private String apartment;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; // Связь с Customer
}
