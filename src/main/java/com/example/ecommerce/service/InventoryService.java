package com.example.ecommerce.service;

import com.example.ecommerce.dto.InventoryAdjustRequest;
import com.example.ecommerce.dto.InventoryHistoryResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.entity.Product;

import java.util.List;

public interface InventoryService {

    // Admin điều chỉnh tồn kho (nhập thêm / trừ bớt)
    ProductDto adjustStock(Long productId, InventoryAdjustRequest request);

    // Danh sách sản phẩm sắp hết hàng (stock <= threshold)
    List<ProductDto> getLowStockProducts(Integer threshold);

    // Lịch sử tồn kho của 1 sản phẩm
    List<InventoryHistoryResponse> getInventoryHistory(Long productId);

    // Ghi log tồn kho khi bán (checkout) - dùng nội bộ
    void recordSale(Product product, int quantitySold);
}
