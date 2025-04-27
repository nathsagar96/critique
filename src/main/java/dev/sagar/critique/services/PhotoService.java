package dev.sagar.critique.services;

import dev.sagar.critique.domain.entities.Photo;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
  Photo uploadPhoto(MultipartFile file);

  Optional<Resource> getPhotoAsResource(String id);
}
