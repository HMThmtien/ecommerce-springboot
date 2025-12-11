package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductFilterRequest;
import com.example.ecommerce.payload.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDto create(ProductDto dto);
    ProductDto update(Long id, ProductDto dto);
    void delete(Long id);
    ProductDto getById(Long id);
    PagedResponse<ProductDto> getAll(int page, int size, String sort);
    ProductDto updateImage(Long productId, String imageUrl);

    PagedResponse<ProductDto> searchProducts(ProductFilterRequest filter);

}
