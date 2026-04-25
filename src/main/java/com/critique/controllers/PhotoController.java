package com.critique.controllers;

import com.critique.dtos.requests.PhotoUploadRequest;
import com.critique.dtos.responses.PhotoResponse;
import com.critique.services.PhotoService;
import com.critique.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
@Tag(name = "Photos", description = "API endpoints for managing photos")
public class PhotoController {

    private final PhotoService photoService;
    private final SecurityUtils securityUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a photo", description = "Upload a photo file with optional caption")
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @Parameter(description = "Photo file to upload", required = true) @RequestPart("file") MultipartFile file,
            @Parameter(description = "Optional caption for the photo", example = "Delicious pasta dish")
                    @RequestPart(value = "caption", required = false)
                    String caption) {

        String userId = securityUtils.getCurrentUserId();

        PhotoUploadRequest request = caption != null ? new PhotoUploadRequest(caption) : null;

        PhotoResponse response = photoService.uploadPhoto(file, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{photoId}")
    @Operation(summary = "Get photo data", description = "Retrieve the binary data of a specific photo")
    public ResponseEntity<byte[]> getPhoto(
            @Parameter(description = "ID of the photo to retrieve", example = "photo123") @PathVariable
                    String photoId) {
        byte[] photoData = photoService.getPhoto(photoId);
        String contentType = photoService.getPhotoContentType(photoId);

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(contentType);
        } catch (Exception _) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok().contentType(mediaType).body(photoData);
    }

    @PatchMapping("/{photoId}")
    @Operation(summary = "Update photo caption", description = "Update the caption of an existing photo")
    public ResponseEntity<PhotoResponse> updateCaption(
            @Parameter(description = "ID of the photo to update", example = "photo123") @PathVariable String photoId,
            @Parameter(description = "Photo upload request with new caption") @Valid @RequestBody
                    PhotoUploadRequest request) {
        String userId = securityUtils.getCurrentUserId();
        PhotoResponse response = photoService.updateCaption(photoId, request.caption(), userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{photoId}")
    @Operation(summary = "Delete a photo", description = "Delete an existing photo")
    public ResponseEntity<Void> deletePhoto(
            @Parameter(description = "ID of the photo to delete", example = "photo123") @PathVariable String photoId) {
        String userId = securityUtils.getCurrentUserId();
        photoService.deletePhoto(photoId, userId);
        return ResponseEntity.noContent().build();
    }
}
