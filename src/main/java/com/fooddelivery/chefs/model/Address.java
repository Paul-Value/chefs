package com.fooddelivery.chefs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "address")
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
}
