package com.fooddelivery.chefs.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRejectRequest {
    @NotBlank(message = "Причина отклонения обязательна")
    private String rejectComment;
}
