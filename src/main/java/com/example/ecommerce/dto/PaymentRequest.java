package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {

    // COD, BANK_TRANSFER, VNPAY, MOMO (mock)
    @NotBlank
    private String method;
}
