package com.campus.market.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");

    public FileStorageService(@Value("${app.upload.dir:./uploads}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload storage directory", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "upload.jpg";
        }

        String extension = "";
        int extIndex = originalFilename.lastIndexOf('.');
        if (extIndex >= 0) {
            extension = originalFilename.substring(extIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only JPG, PNG or WebP images are allowed.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Image must be smaller than 10 MB.");
        }

        String uniqueFileName = UUID.randomUUID().toString() + extension;
        Path targetLocation = this.uploadDir.resolve(uniqueFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded image file", e);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.startsWith("/uploads/demo/")) {
            // Never delete pre-packaged demo images
            return;
        }

        if (fileUrl.startsWith("/uploads/")) {
            String fileName = fileUrl.substring("/uploads/".length());
            try {
                Path filePath = this.uploadDir.resolve(fileName).normalize();
                if (filePath.startsWith(this.uploadDir)) {
                    Files.deleteIfExists(filePath);
                }
            } catch (IOException ignored) {
            }
        }
    }
}

