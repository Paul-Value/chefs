package com.fooddelivery.chefs.model;

import com.fooddelivery.chefs.model.dto.ChefResponse;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "chef", fetch = FetchType.LAZY)
    private List<Food> foods = new ArrayList<>();

    @Transient
    public String getRole() {
        return "CHEF";
    }

    public ChefResponse toResponse() {
        return ChefResponse.builder()
                .chefId(this.getUserId())
                .photoUrl(this.getPhotoUrl())
                .description(this.getDescription())
                .phone(this.getPhone())
                .isWorking(this.getIsWorking())
                .build();
    }
}
