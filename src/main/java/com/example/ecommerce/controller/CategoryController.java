package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ADMIN: tạo category
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        return ApiResponse.ok("Tạo category thành công", categoryService.create(dto));
    }

    // ADMIN: update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<CategoryDto> update(@PathVariable Long id,
                                           @Valid @RequestBody CategoryDto dto) {
        return ApiResponse.ok("Cập nhật category thành công", categoryService.update(id, dto));
    }

    // ADMIN: xóa
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.ok("Xóa category thành công", null);
    }

    // PUBLIC: lấy danh sách
    @GetMapping
    public ApiResponse<List<CategoryDto>> getAll() {
        return ApiResponse.ok("Lấy danh sách category thành công", categoryService.getAll());
    }

    // PUBLIC: lấy 1 category
    @GetMapping("/{id}")
    public ApiResponse<CategoryDto> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy category thành công", categoryService.getById(id));
    }
}
