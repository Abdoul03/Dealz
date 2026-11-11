package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.response.AnnonceResponseDTO;
import com.doul.dealz.services.FavoriService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("favoris")
@RequiredArgsConstructor
public class FavoriController {

    private final FavoriService favoriService;

    @GetMapping
    public ResponseEntity<List<AnnonceResponseDTO>> getMesFavoris(Authentication authentication) {
        return ResponseEntity.ok(favoriService.getMesFavoris(userId(authentication)));
    }

    @GetMapping("/ids")
    public ResponseEntity<List<String>> getMesFavorisIds(Authentication authentication) {
        return ResponseEntity.ok(favoriService.getMesFavorisIds(userId(authentication)));
    }

    @PostMapping("/{annonceId}")
    public ResponseEntity<Void> ajouter(
            @PathVariable String annonceId,
            Authentication authentication) {
        favoriService.ajouter(annonceId, userId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{annonceId}")
    public ResponseEntity<Void> retirer(
            @PathVariable String annonceId,
            Authentication authentication) {
        favoriService.retirer(annonceId, userId(authentication));
        return ResponseEntity.noContent().build();
    }

    private String userId(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }
}
