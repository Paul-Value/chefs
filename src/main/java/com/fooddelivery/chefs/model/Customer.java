package com.fooddelivery.chefs.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "customer")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "customer_id")
public class Customer extends User {
    @Column(name = "device_id", unique = true, nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String phone;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;


    @Transient
    public String getRole() {
        return "CUSTOMER";
    }
}
