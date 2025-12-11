package com.example.ecommerce.controller;

import com.example.ecommerce.dto.InventoryAdjustRequest;
import com.example.ecommerce.dto.InventoryHistoryResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    private final InventoryService inventoryService;

    // Điều chỉnh tồn kho (nhập thêm / trừ đi)
    @PostMapping("/products/{productId}/adjust")
    public ApiResponse<ProductDto> adjustStock(@PathVariable Long productId,
                                               @RequestBody @Valid InventoryAdjustRequest request) {
        ProductDto dto = inventoryService.adjustStock(productId, request);
        return ApiResponse.ok("Điều chỉnh tồn kho thành công", dto);
    }

    // Danh sách sản phẩm sắp hết hàng
    @GetMapping("/low-stock")
    public ApiResponse<List<ProductDto>> getLowStockProducts(
            @RequestParam(required = false) Integer threshold) {

        List<ProductDto> products = inventoryService.getLowStockProducts(threshold);
        return ApiResponse.ok("Lấy danh sách sản phẩm sắp hết hàng thành công", products);
    }

    // Lịch sử tồn kho của 1 sản phẩm
    @GetMapping("/products/{productId}/history")
    public ApiResponse<List<InventoryHistoryResponse>> getHistory(@PathVariable Long productId) {
        List<InventoryHistoryResponse> history = inventoryService.getInventoryHistory(productId);
        return ApiResponse.ok("Lấy lịch sử tồn kho thành công", history);
    }
}
