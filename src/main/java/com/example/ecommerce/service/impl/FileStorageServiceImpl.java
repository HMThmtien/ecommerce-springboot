package com.example.ecommerce.service.impl;

import com.example.ecommerce.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadPath;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());

        // Validate file type - only allow images
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ cho phép upload file ảnh (image/*)");
        }

        String ext = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex > 0) {
            ext = original.substring(dotIndex);
        }

        String fileName = UUID.randomUUID() + ext;

        try {
            Path target = this.uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Lỗi lưu file", e);
            throw new RuntimeException("Lỗi lưu file", e);
        }

        // URL để FE truy cập
        return "/uploads/" + fileName;
    }
}
