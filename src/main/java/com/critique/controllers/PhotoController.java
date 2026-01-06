package com.critique.controllers;

import com.critique.dtos.requests.PhotoUploadRequest;
import com.critique.dtos.responses.PhotoResponse;
import com.critique.services.PhotoService;
import com.critique.utils.SecurityUtils;
import jakarta.validation.Valid; // Added import for @Valid
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final SecurityUtils securityUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @RequestPart("file") MultipartFile file, @RequestPart(value = "caption", required = false) String caption) {

        String userId = securityUtils.getCurrentUserId();

        PhotoUploadRequest request = caption != null ? new PhotoUploadRequest(caption) : null;

        PhotoResponse response = photoService.uploadPhoto(file, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String photoId) {
        byte[] photoData = photoService.getPhoto(photoId);
        String contentType = photoService.getPhotoContentType(photoId);

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok().contentType(mediaType).body(photoData);
    }

    @PatchMapping("/{photoId}")
    public ResponseEntity<PhotoResponse> updateCaption(
            @PathVariable String photoId, @Valid @RequestBody PhotoUploadRequest request) {
        String userId = securityUtils.getCurrentUserId();
        PhotoResponse response = photoService.updateCaption(photoId, request.caption(), userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable String photoId) {
        String userId = securityUtils.getCurrentUserId();
        photoService.deletePhoto(photoId, userId);
        return ResponseEntity.noContent().build();
    }
}
