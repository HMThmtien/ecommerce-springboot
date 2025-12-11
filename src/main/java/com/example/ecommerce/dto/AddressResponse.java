package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class AddressResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String street;
    private boolean defaultAddress;
}
