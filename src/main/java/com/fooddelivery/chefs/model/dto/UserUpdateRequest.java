package com.fooddelivery.chefs.model.dto;

import lombok.Data;
import org.hibernate.annotations.processing.Pattern;

@Data
public class UserUpdateRequest {
    @NotBlank
    private String name;

    @Pattern(regexp = "^\\+7\\d{10}$")
    private String phone;
}
