package dev.sagar.critique.services;

import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  String store(MultipartFile file, String filename);

  Optional<Resource> loadAsResource(String id);
}
