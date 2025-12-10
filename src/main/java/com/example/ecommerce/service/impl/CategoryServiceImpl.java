package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private CategoryDto mapToDto(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        return dto;
    }

    private Category mapToEntity(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category đã tồn tại với tên: " + dto.getName());
        }
        Category cat = mapToEntity(dto);
        return mapToDto(categoryRepository.save(cat));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!cat.getName().equalsIgnoreCase(dto.getName())
                && categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category đã tồn tại với tên: " + dto.getName());
        }

        cat.setName(dto.getName());
        cat.setDescription(dto.getDescription());
        return mapToDto(categoryRepository.save(cat));
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDto(cat);
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }
}
