package com.critique.controllers;

import com.critique.dtos.responses.PhotoResponse;
import com.critique.mappers.PhotoMapper;
import com.critique.services.PhotoService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final PhotoMapper photoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        return photoMapper.toResponse(photoService.uploadPhoto(file));
    }

    @GetMapping(path = "/{id:.+}")
    public ResponseEntity<Resource> getPhotoById(@PathVariable UUID id) {
        return photoService
                .getPhotoAsResource(id)
                .map(photo -> ResponseEntity.ok()
                        .contentType(MediaTypeFactory.getMediaType(photo).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                        .body(photo))
                .orElse(ResponseEntity.notFound().build());
    }
}
