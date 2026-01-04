package com.critique.services;

import com.critique.dtos.requests.PhotoUploadRequest;
import com.critique.dtos.responses.PhotoResponse;
import com.critique.entities.Photo;
import com.critique.exceptions.BusinessException;
import com.critique.mappers.PhotoMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoMapper photoMapper;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5242880}") // 5MB default
    private long maxFileSize;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final Map<String, PhotoMetadata> photoStorage = new HashMap<>();

    public PhotoResponse uploadPhoto(MultipartFile file, PhotoUploadRequest request, String userId) {
        log.info("Uploading photo for user '{}'", userId);

        validateFile(file);

        try {
            String photoId = UUID.randomUUID().toString();
            String originalFileName = file.getOriginalFilename();
            String fileName = photoId + "_" + originalFileName;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            PhotoMetadata metadata = new PhotoMetadata(fileName, file.getContentType(), filePath.toString());
            photoStorage.put(photoId, metadata);

            Photo photo = Photo.builder()
                    .id(photoId)
                    .url("/api/v1/photos/" + photoId + "/file")
                    .caption(request != null ? request.caption() : null)
                    .uploadedAt(Instant.now())
                    .uploadedBy(userId)
                    .build();

            log.info("Photo uploaded successfully with ID: {} as file: {}", photoId, fileName);
            return photoMapper.toResponse(photo);

        } catch (IOException e) {
            log.error("Failed to upload photo", e);
            throw new BusinessException("Failed to upload photo: " + e.getMessage());
        }
    }

    public Photo getPhotoById(String photoId) {
        PhotoMetadata metadata = photoStorage.get(photoId);
        if (metadata == null) {
            throw new BusinessException("Photo not found with ID: " + photoId);
        }

        return Photo.builder()
                .id(photoId)
                .url("/api/v1/photos/" + photoId + "/file")
                .build();
    }

    public List<Photo> getPhotosByIds(List<String> photoIds) {
        if (photoIds == null || photoIds.isEmpty()) {
            return List.of();
        }

        return photoIds.stream().map(this::getPhotoById).toList();
    }

    public byte[] getPhotoFile(String photoId) {
        PhotoMetadata metadata = photoStorage.get(photoId);
        if (metadata == null) {
            throw new BusinessException("Photo not found with ID: " + photoId);
        }

        try {
            Path filePath = Paths.get(metadata.filePath());
            log.debug("Reading photo file from: {}", filePath);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read photo file: {}", metadata.filePath(), e);
            throw new BusinessException("Failed to read photo file: " + e.getMessage());
        }
    }

    public String getPhotoContentType(String photoId) {
        PhotoMetadata metadata = photoStorage.get(photoId);
        if (metadata == null) {
            throw new BusinessException("Photo not found with ID: " + photoId);
        }
        return metadata.contentType();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File is required");
        }

        if (file.getSize() > maxFileSize) {
            throw new BusinessException(
                    String.format("File size exceeds maximum allowed size of %d bytes", maxFileSize));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(
                    "Invalid file type. Allowed types: " + String.join(", ", ALLOWED_CONTENT_TYPES));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("File must have a valid name");
        }
    }

    private record PhotoMetadata(String fileName, String contentType, String filePath) {}
}
