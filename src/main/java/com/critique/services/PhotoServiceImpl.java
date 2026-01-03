package com.critique.services;

import com.critique.entities.Photo;
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
        UUID photoId = UUID.randomUUID();
        String url = storageService.store(file, photoId.toString());

        return Photo.builder().url(url).uploadedDate(LocalDateTime.now()).build();
    }

    @Override
    public Optional<Resource> getPhotoAsResource(UUID id) {
        return storageService.loadAsResource(id.toString());
    }
}
