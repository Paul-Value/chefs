package com.fooddelivery.chefs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "chef")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "chef_id")
public class Chef extends User {
    @Column(name = "photo_url")
    private String photoUrl;

    private String description;

    @Column(nullable = false)
    private String phone;

    @Column(name = "access_code", unique = true, nullable = false)
    private String accessCode;

    @Column(name = "is_working", columnDefinition = "boolean default false")
    private Boolean isWorking = false;
}
