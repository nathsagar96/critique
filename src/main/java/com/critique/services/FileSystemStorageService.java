package com.critique.services;

import com.critique.exceptions.StorageException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    @Value("${app.storage.location:uploads}")
    private String storageLocation;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException ioException) {
            throw new StorageException("Could not initialize storage location", ioException);
        }
    }

    @Override
    public String store(MultipartFile file, String fileName) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Cannot save an empty file");
            }

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String finalFileName = fileName + "." + extension;

            Path destinationFile =
                    rootLocation.resolve(Paths.get(finalFileName).normalize().toAbsolutePath());

            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside specified directory");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return finalFileName;
        } catch (IOException ioException) {
            throw new StorageException("Failed to store file", ioException);
        }
    }

    @Override
    public Optional<Resource> loadAsResource(String fileName) {
        try {
            Path file = rootLocation.resolve(fileName);

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return Optional.of(resource);
            } else {
                return Optional.empty();
            }
        } catch (MalformedURLException malformedURLException) {
            log.warn("Could not read file: %s".formatted(fileName), malformedURLException);
            return Optional.empty();
        }
    }
}
