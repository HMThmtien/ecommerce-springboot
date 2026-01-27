package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.payload.PagedResponse;

public interface UserService {

    // USER (self)
    UserDto getMe();
    UserDto updateMe(UserUpdateRequest req);
    void changeMyPassword(ChangePasswordRequest req);

    // ADMIN
    PagedResponse<UserDto> adminGetAll(Integer page, Integer size, String sort, String dir);
    UserDto adminGetById(Long id);
    UserDto adminSetEnabled(Long id, boolean enabled);
    UserDto adminSetRoles(Long id, AdminSetRolesRequest req);
    void adminResetPassword(Long id, String newPassword);
}
