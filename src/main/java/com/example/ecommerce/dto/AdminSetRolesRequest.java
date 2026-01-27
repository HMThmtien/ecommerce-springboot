package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class AdminSetRolesRequest {
    @NotEmpty
    private Set<String> roles; // ROLE_USER, ROLE_ADMIN
}
