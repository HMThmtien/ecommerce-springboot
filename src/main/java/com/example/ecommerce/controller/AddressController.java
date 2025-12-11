package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddressRequest;
import com.example.ecommerce.dto.AddressResponse;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // Tạo địa chỉ mới
    @PostMapping
    public ApiResponse<AddressResponse> create(@Valid @RequestBody AddressRequest request) {
        return ApiResponse.ok("Tạo địa chỉ thành công", addressService.create(request));
    }

    // Cập nhật địa chỉ
    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody AddressRequest request) {
        return ApiResponse.ok("Cập nhật địa chỉ thành công", addressService.update(id, request));
    }

    // Xoá địa chỉ
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ApiResponse.ok("Xoá địa chỉ thành công", null);
    }

    // Lấy tất cả địa chỉ của mình
    @GetMapping
    public ApiResponse<List<AddressResponse>> getMyAddresses() {
        return ApiResponse.ok("Lấy danh sách địa chỉ thành công", addressService.getMyAddresses());
    }

    // Lấy 1 địa chỉ theo id (của mình)
    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok("Lấy địa chỉ thành công", addressService.getMyAddressById(id));
    }

    // Đặt địa chỉ mặc định
    @PostMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(@PathVariable Long id) {
        return ApiResponse.ok("Cập nhật địa chỉ mặc định thành công", addressService.setDefault(id));
    }

    // Lấy địa chỉ mặc định
    @GetMapping("/default")
    public ApiResponse<AddressResponse> getDefault() {
        return ApiResponse.ok("Lấy địa chỉ mặc định thành công", addressService.getMyDefaultAddress());
    }
}
