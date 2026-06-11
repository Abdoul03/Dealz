package com.doul.dealz.controllers;

import com.doul.dealz.services.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Endpoint d'upload d'images. Sécurisé (non listé dans /images/** permitAll).
 * Les images sont servies publiquement via GET /images/{filename} (resource handler).
 */
@RestController
@RequestMapping("media")
@RequiredArgsConstructor
public class MediaController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {

        String filename = imageService.saveImage(file);
        String url = buildImageUrl(request, filename);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("url", url, "filename", filename));
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<List<Map<String, String>>> uploadMultiple(
            @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request) throws IOException {

        if (files.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni.");
        }
        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filename = imageService.saveImage(file);
                results.add(Map.of("url", buildImageUrl(request, filename), "filename", filename));
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(results);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String filename) throws IOException {
        imageService.deleteImage(filename);
        return ResponseEntity.noContent().build();
    }

    private String buildImageUrl(HttpServletRequest request, String filename) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(request.getContextPath() + "/images/" + filename)
                .toUriString();
    }
}
