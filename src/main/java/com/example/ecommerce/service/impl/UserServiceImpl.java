package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.payload.PagedResponse;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private User getCurrentUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // thường là email/username trong JWT
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setFullName(u.getFullName());
        dto.setEnabled(u.isEnabled());
        dto.setRoles(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }

    // ---------------- USER (self) ----------------

    @Override
    public UserDto getMe() {
        return toDto(getCurrentUserEntity());
    }

    @Override
    public UserDto updateMe(UserUpdateRequest req) {
        User u = getCurrentUserEntity();
        u.setFullName(req.getFullName());
        return toDto(userRepository.save(u));
    }

    @Override
    public void changeMyPassword(ChangePasswordRequest req) {
        User u = getCurrentUserEntity();

        if (!passwordEncoder.matches(req.getOldPassword(), u.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không đúng");
        }

        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
    }

    // ---------------- ADMIN ----------------

    @Override
    public PagedResponse<UserDto> adminGetAll(Integer page, Integer size, String sort, String dir) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;

        String sortField = (sort == null || sort.isBlank()) ? "id" : sort;
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(p, s, Sort.by(direction, sortField));
        Page<User> userPage = userRepository.findAll(pageable);

        return new PagedResponse<>(
                userPage.getContent().stream().map(this::toDto).toList(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Override
    public UserDto adminGetById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toDto(u);
    }

    @Override
    public UserDto adminSetEnabled(Long id, boolean enabled) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        u.setEnabled(enabled);
        return toDto(userRepository.save(u));
    }

    @Override
    public UserDto adminSetRoles(Long id, AdminSetRolesRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Set<Role> roles = req.getRoles().stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name)))
                .collect(Collectors.toSet());

        u.setRoles(roles);
        return toDto(userRepository.save(u));
    }

    @Override
    public void adminResetPassword(Long id, String newPassword) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
    }
}
