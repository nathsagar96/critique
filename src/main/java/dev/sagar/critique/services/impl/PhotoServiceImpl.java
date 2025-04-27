package dev.sagar.critique.services.impl;

import dev.sagar.critique.domain.entities.Photo;
import dev.sagar.critique.services.PhotoService;
import dev.sagar.critique.services.StorageService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

  private final StorageService storageService;

  @Override
  public Photo uploadPhoto(MultipartFile file) {
    String photoId = UUID.randomUUID().toString();
    String url = storageService.store(file, photoId);

    return Photo.builder().url(url).uploadDate(LocalDateTime.now()).build();
  }

  @Override
  public Optional<Resource> getPhotoAsResource(String id) {
    return storageService.loadAsResource(id);
  }
}
