package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được trống")
    private String name;

    private String description;

    @NotNull(message = "Giá sản phẩm không được null")
    @Positive(message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được null")
    @PositiveOrZero(message = "Số lượng tồn kho phải >= 0")
    private Integer stock;

    private String imageUrl;
    private Long categoryId;
    private String categoryName;
}
