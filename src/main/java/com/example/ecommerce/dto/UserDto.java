package com.example.ecommerce.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private boolean enabled;
    private Set<String> roles; // ví dụ: ["ROLE_USER","ROLE_ADMIN"]
}
