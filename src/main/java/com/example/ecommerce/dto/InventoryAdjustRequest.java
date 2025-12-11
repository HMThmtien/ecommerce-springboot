package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryAdjustRequest {

    // Số lượng thay đổi: dương = cộng thêm, âm = trừ bớt
    @NotNull
    private Integer quantityChange;

    // Ghi chú: "Nhập thêm kho", "Kiểm kê điều chỉnh",...
    private String note;
}
