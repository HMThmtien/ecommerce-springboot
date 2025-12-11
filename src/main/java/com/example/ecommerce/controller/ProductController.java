package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductFilterRequest;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.payload.PagedResponse;
import com.example.ecommerce.service.FileStorageService;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        return ApiResponse.ok("Tạo sản phẩm thành công", productService.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<ProductDto> update(@PathVariable Long id,
                                          @Valid @RequestBody ProductDto dto) {
        return ApiResponse.ok("Cập nhật sản phẩm thành công", productService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok("Xoá sản phẩm thành công", null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDto> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy sản phẩm thành công", productService.getById(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<ProductDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {
        return ApiResponse.ok("Lấy danh sách sản phẩm thành công",
                productService.getAll(page, size, sort));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/image")
    public ApiResponse<ProductDto> uploadImage(@PathVariable Long id,
                                               @RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeFile(file);
        ProductDto updated = productService.updateImage(id, url);
        return ApiResponse.ok("Cập nhật ảnh sản phẩm thành công", updated);
    }



    @GetMapping("/search")
    public ApiResponse<PagedResponse<ProductDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setKeyword(keyword);
        filter.setCategoryId(categoryId);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDir(sortDir);

        PagedResponse<ProductDto> result = productService.searchProducts(filter);
        return ApiResponse.ok("Tìm kiếm sản phẩm thành công", result);
    }
}
