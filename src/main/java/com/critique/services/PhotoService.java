package com.critique.services;

import com.critique.dtos.requests.PhotoUploadRequest;
import com.critique.dtos.responses.PhotoResponse;
import com.critique.entities.Photo;
import com.critique.exceptions.BusinessException;
import com.critique.exceptions.UnauthorizedException;
import com.critique.mappers.PhotoMapper;
import com.critique.repositories.PhotoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoMapper photoMapper;
    private final PhotoRepository photoRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5242880}") // 5MB default
    private long maxFileSize;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    public PhotoResponse uploadPhoto(MultipartFile file, PhotoUploadRequest request, String userId) {
        log.info("Uploading photo for user '{}'", userId);

        validateFile(file);

        try {
            String photoId = UUID.randomUUID().toString();
            String originalFileName = file.getOriginalFilename();
            String fileExtension = extractFileExtension(originalFileName);
            String fileName = photoId + fileExtension;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Photo photo = Photo.builder()
                    .id(photoId)
                    .caption(request != null ? request.caption() : null)
                    .uploadedAt(Instant.now())
                    .uploadedBy(userId)
                    .fileName(fileName)
                    .contentType(file.getContentType())
                    .filePath(filePath.toString())
                    .build();

            photoRepository.save(photo);

            log.info("Photo uploaded successfully with ID: {} as file: {}", photoId, fileName);
            return photoMapper.toResponse(photo);

        } catch (IOException e) {
            log.error("Failed to upload photo", e);
            throw new BusinessException("Failed to upload photo: " + e.getMessage());
        }
    }

    private String extractFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex);
    }

    public List<Photo> getPhotosByIds(List<String> photoIds) {
        if (photoIds == null || photoIds.isEmpty()) {
            return List.of();
        }

        Iterable<Photo> photosIterable = photoRepository.findAllById(photoIds);
        List<Photo> photos = new ArrayList<>();
        photosIterable.forEach(photos::add);
        return photos;
    }

    public byte[] getPhoto(String photoId) {
        Photo photo = photoRepository
                .findById(photoId)
                .orElseThrow(() -> new BusinessException("Photo not found with ID: " + photoId));

        try {
            Path filePath = Paths.get(photo.getFilePath());
            log.debug("Reading photo file from: {}", filePath);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read photo file: {}", photo.getFilePath(), e);
            throw new BusinessException("Failed to read photo file: " + e.getMessage());
        }
    }

    public String getPhotoContentType(String photoId) {
        Photo photo = photoRepository
                .findById(photoId)
                .orElseThrow(() -> new BusinessException("Photo not found with ID: " + photoId));
        return photo.getContentType();
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

    public PhotoResponse updateCaption(String photoId, String caption, String userId) {
        Photo photo = photoRepository
                .findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with ID: " + photoId));

        if (!photo.getUploadedBy().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this photo");
        }

        photo.setCaption(caption);
        Photo updatedPhoto = photoRepository.save(photo);
        log.info("Photo caption updated for photo ID: {}", photoId);
        return photoMapper.toResponse(updatedPhoto);
    }

    public void deletePhoto(String photoId, String userId) {
        Photo photo = photoRepository
                .findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with ID: " + photoId));

        if (!photo.getUploadedBy().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this photo");
        }

        try {
            Path filePath = Paths.get(photo.getFilePath());
            Files.deleteIfExists(filePath);
            photoRepository.delete(photo);
            log.info("Photo deleted successfully with ID: {}", photoId);
        } catch (IOException e) {
            log.error("Failed to delete photo file: {}", photo.getFilePath(), e);
            throw new BusinessException("Failed to delete photo: " + e.getMessage());
        }
    }
}
