package com.doul.dealz.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageService {

    private static final Set<String> EXTENSIONS_AUTORISEES = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final long TAILLE_MAX_OCTETS = 10 * 1024 * 1024; // 10 MB

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }
        if (file.getSize() > TAILLE_MAX_OCTETS) {
            throw new IllegalArgumentException("L'image ne doit pas dépasser 10 Mo.");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "image");
        String extension = extractExtension(originalFilename);

        if (!EXTENSIONS_AUTORISEES.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Format non supporté. Formats acceptés : jpg, jpeg, png, webp.");
        }

        Path directory = Paths.get(uploadDir);
        Files.createDirectories(directory);

        String filename = UUID.randomUUID() + "." + extension.toLowerCase();
        Files.copy(file.getInputStream(), directory.resolve(filename));
        return filename;
    }

    public void deleteImage(String filename) throws IOException {
        // On extrait uniquement le nom du fichier pour éviter les path traversal
        Path filePath = Paths.get(uploadDir)
                .resolve(Paths.get(filename).getFileName());
        Files.deleteIfExists(filePath);
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            throw new IllegalArgumentException("Impossible de déterminer l'extension du fichier.");
        }
        return filename.substring(dot + 1);
    }
}
