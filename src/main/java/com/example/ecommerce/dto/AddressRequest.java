package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[0-9+]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank
    private String province;

    @NotBlank
    private String district;

    @NotBlank
    private String ward;

    @NotBlank
    private String street;

    // có set làm mặc định luôn khi tạo/sửa không
    private Boolean defaultAddress;
}
