package dev.sagar.critique.controllers;

import dev.sagar.critique.domain.dtos.PhotoDto;
import dev.sagar.critique.domain.entities.Photo;
import dev.sagar.critique.mappers.PhotoMapper;
import dev.sagar.critique.services.PhotoService;
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
  public PhotoDto uploadPhoto(@RequestParam("file") MultipartFile file) {
    Photo photo = photoService.uploadPhoto(file);
    return photoMapper.toDto(photo);
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<Resource> getPhotoAsResource(@PathVariable String id) {
    return photoService
        .getPhotoAsResource(id)
        .map(
            response ->
                ResponseEntity.ok()
                    .contentType(
                        MediaTypeFactory.getMediaType(response)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(response))
        .orElse(ResponseEntity.notFound().build());
  }
}
