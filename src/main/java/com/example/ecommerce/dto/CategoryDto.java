package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Tên category không được trống")
    private String name;

    private String description;
}
