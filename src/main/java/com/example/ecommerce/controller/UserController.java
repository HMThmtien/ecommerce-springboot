package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.payload.PagedResponse;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ----------- USER (self) -----------

    @GetMapping("/me")
    public ApiResponse<UserDto> me() {
        return ApiResponse.ok("Lấy thông tin cá nhân thành công", userService.getMe());
    }

    @PutMapping("/me")
    public ApiResponse<UserDto> updateMe(@Valid @RequestBody UserUpdateRequest req) {
        return ApiResponse.ok("Cập nhật thông tin cá nhân thành công", userService.updateMe(req));
    }

    @PostMapping("/me/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        userService.changeMyPassword(req);
        return ApiResponse.ok("Đổi mật khẩu thành công", null);
    }

    // ----------- ADMIN -----------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<PagedResponse<UserDto>> adminGetAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        return ApiResponse.ok("Lấy danh sách user thành công", userService.adminGetAll(page, size, sort, dir));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<UserDto> adminGetById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy user thành công", userService.adminGetById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/enabled")
    public ApiResponse<UserDto> adminSetEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        return ApiResponse.ok("Cập nhật trạng thái user thành công", userService.adminSetEnabled(id, enabled));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ApiResponse<UserDto> adminSetRoles(@PathVariable Long id, @Valid @RequestBody AdminSetRolesRequest req) {
        return ApiResponse.ok("Cập nhật role thành công", userService.adminSetRoles(id, req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reset-password")
    public ApiResponse<Void> adminResetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.adminResetPassword(id, newPassword);
        return ApiResponse.ok("Reset mật khẩu thành công", null);
    }
}
