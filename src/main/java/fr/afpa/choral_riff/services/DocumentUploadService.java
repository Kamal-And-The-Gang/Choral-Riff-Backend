// package fr.afpa.choral_riff.services;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.UUID;

// @Service
// public class DocumentUploadService {

//     @Value("${upload.dir}")
//     private String uploadDir;

//     public String saveFile(MultipartFile file) throws IOException {

//         String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

//         Path path = Paths.get(uploadDir, fileName);

//         Files.createDirectories(path.getParent());

//         Files.write(path, file.getBytes());

//         return fileName;
//     }
// }

package fr.afpa.choral_riff.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentUploadService {

    @Value("${upload.dir}")
    private String uploadDir;

    // --- Méthode optimisée pour gros fichiers ---
    public String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);

        // Crée le dossier s'il n'existe pas
        Files.createDirectories(path.getParent());

        // Copie le flux d'entrée directement sur le disque (évite de charger tout le fichier en mémoire)
        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, path);
        }

        return fileName;
    }
}