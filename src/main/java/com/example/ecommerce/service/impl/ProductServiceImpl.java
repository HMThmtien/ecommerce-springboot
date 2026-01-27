package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductFilterRequest;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.payload.PagedResponse;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.spec.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "name", "price", "stock");

    private Pageable buildPageable(
            Integer page,
            Integer size,
            String sort,
            String dir
    ) {
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 10 : size;

        String sortField = ALLOWED_SORT_FIELDS.contains(sort) ? sort : "id";
        Sort.Direction direction =
                "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
    }

    private PagedResponse<ProductDto> mapToPagedResponse(Page<Product> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::mapToDto).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }




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

    private Product mapToEntity(ProductDto dto) {
        Product p = new Product();
        p.setId(dto.getId());
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setImageUrl(dto.getImageUrl());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
            p.setCategory(category);
        }
        return p;
    }

    @Override
    //@CacheEvict(value = "products", allEntries = true)
    public ProductDto create(ProductDto dto) {
        Product product = mapToEntity(dto);
        Product saved = productRepository.save(product);
        return mapToDto(saved);
    }

    @Override
    //@CacheEvict(value = "products", key = "#id")
    public ProductDto update(Long id, ProductDto dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
            existing.setCategory(category);
        }
        return mapToDto(productRepository.save(existing));
    }

    @Override
    //@CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Override
    //@Cacheable(value = "product_by_id", key = "#id")
    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDto(product);
    }

    @Override
    public PagedResponse<ProductDto> getAllProducts(
            Integer page,
            Integer size,
            String sort,
            String dir
    ) {
        Pageable pageable = buildPageable(page, size, sort, dir);

        Page<Product> productPage = productRepository.findAllProducts(pageable);

        return mapToPagedResponse(productPage);
    }


    @Override
    public ProductDto updateImage(Long productId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setImageUrl(imageUrl);
        return mapToDto(productRepository.save(product));
    }



    @Override
    public PagedResponse<ProductDto> searchProducts(ProductFilterRequest filter) {

        Pageable pageable = buildPageable(
                filter.getPage(),
                filter.getSize(),
                filter.getSortBy(),
                filter.getSortDir()
        );

        Page<Product> page = productRepository.searchProducts(
                filter.getKeyword(),
                filter.getCategoryId(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                pageable
        );

        return mapToPagedResponse(page);
    }



}
