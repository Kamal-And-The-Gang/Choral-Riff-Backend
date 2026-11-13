package fr.afpa.choral_riff.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentUploadService {

  @Value("${upload.dir}")
  private String uploadDir;

  public String saveFile(MultipartFile file, String folder) throws IOException {
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

    Path path = Paths.get(uploadDir, folder, fileName);

    Files.createDirectories(path.getParent());

    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, path);
    }

    return fileName;
  }
}
