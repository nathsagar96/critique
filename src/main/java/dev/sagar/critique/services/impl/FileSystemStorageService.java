package dev.sagar.critique.services.impl;

import dev.sagar.critique.exceptions.StorageException;
import dev.sagar.critique.services.StorageService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
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
    rootLocation = Path.of(storageLocation);
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage location", e);
    }
  }

  @Override
  public String store(MultipartFile file, String filename) {
    if (file.isEmpty()) {
      throw new StorageException("Cannot save an empty file");
    }

    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    String finalFilename = filename + "." + extension;

    Path destinationFile =
        rootLocation.resolve(Paths.get(finalFilename)).normalize().toAbsolutePath();

    if (destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
      throw new StorageException("Cannot store file outside specified directory");
    }

    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException("Failed to store file", e);
    }

    return finalFilename;
  }

  @Override
  public Optional<Resource> loadAsResource(String filename) {
    try {
      Path file = rootLocation.resolve(filename);

      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return Optional.of(resource);
      } else {
        return Optional.empty();
      }
    } catch (MalformedURLException e) {
      log.warn("Could not read file: {}", filename, e);
      return Optional.empty();
    }
  }
}
