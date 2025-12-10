package com.example.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object value) {
        super(String.format("%s không tồn tại với %s = '%s'", resourceName, fieldName, value));
    }
}
