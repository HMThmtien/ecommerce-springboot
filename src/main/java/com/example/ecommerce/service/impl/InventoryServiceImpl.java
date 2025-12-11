package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.InventoryAdjustRequest;
import com.example.ecommerce.dto.InventoryHistoryResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.entity.InventoryHistory;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.InventoryHistoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;

    // Bạn đã có mapToDto(Product) ở ProductServiceImpl,
    // ở đây có thể viết lại Hàm map rất đơn giản hoặc tái sử dụng nếu muốn.
    private ProductDto mapToDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setImageUrl(p.getImageUrl());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }
        return dto;
    }

    private InventoryHistoryResponse mapHistoryToResponse(InventoryHistory h) {
        InventoryHistoryResponse res = new InventoryHistoryResponse();
        res.setId(h.getId());
        res.setProductId(h.getProduct().getId());
        res.setProductName(h.getProduct().getName());
        res.setQuantityChange(h.getQuantityChange());
        res.setType(h.getType());
        res.setNote(h.getNote());
        res.setCreatedAt(h.getCreatedAt());
        return res;
    }

    @Override
    @Transactional
    public ProductDto adjustStock(Long productId, InventoryAdjustRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        int change = request.getQuantityChange();
        int newStock = product.getStock() + change;

        if (newStock < 0) {
            throw new BadRequestException("Không thể điều chỉnh tồn kho: số lượng sau điều chỉnh âm");
        }

        product.setStock(newStock);
        productRepository.save(product);

        InventoryHistory history = InventoryHistory.builder()
                .product(product)
                .quantityChange(change)
                .type("ADJUST")
                .note(request.getNote())
                .createdAt(Instant.now())
                .build();
        inventoryHistoryRepository.save(history);

        return mapToDto(product);
    }

    @Override
    public List<ProductDto> getLowStockProducts(Integer threshold) {
        int th = (threshold == null || threshold <= 0) ? 10 : threshold;

        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() <= th)
                .toList();

        return products.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<InventoryHistoryResponse> getInventoryHistory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return inventoryHistoryRepository.findByProductOrderByCreatedAtDesc(product)
                .stream()
                .map(this::mapHistoryToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void recordSale(Product product, int quantitySold) {
        InventoryHistory history = InventoryHistory.builder()
                .product(product)
                .quantityChange(-quantitySold) // bán ra => âm
                .type("SALE")
                .note("Bán hàng qua order")
                .createdAt(Instant.now())
                .build();
        inventoryHistoryRepository.save(history);
    }
}
