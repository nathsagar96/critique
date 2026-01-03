package com.critique.services;

import com.critique.entities.Photo;
import java.util.Optional;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    Photo uploadPhoto(MultipartFile file);

    Optional<Resource> getPhotoAsResource(UUID id);
}
