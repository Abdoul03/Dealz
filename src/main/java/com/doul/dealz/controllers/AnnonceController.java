package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.request.AnnonceRequestDTO;
import com.doul.dealz.model.dto.response.AnnonceResponseDTO;
import com.doul.dealz.services.AnnonceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("annonces")
@RequiredArgsConstructor
public class AnnonceController {

    private final AnnonceService annonceService;

    @PostMapping
    public ResponseEntity<AnnonceResponseDTO> create(
            @Valid @RequestBody AnnonceRequestDTO dto,
            Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        return ResponseEntity.status(HttpStatus.CREATED).body(annonceService.createAnnonce(dto, userId));
    }

    @GetMapping
    public ResponseEntity<List<AnnonceResponseDTO>> getAll(
            @RequestParam(required = false) String categorieId,
            @RequestParam(required = false) String motCle) {
        return ResponseEntity.ok(annonceService.getAnnonces(categorieId, motCle));
    }

    @GetMapping("/mes-annonces")
    public ResponseEntity<List<AnnonceResponseDTO>> getMesAnnonces(Authentication authentication) {
        return ResponseEntity.ok(annonceService.getMesAnnonces(authentication.getPrincipal().toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnonceResponseDTO> getOne(@PathVariable String id) {
        return ResponseEntity.ok(annonceService.getAnnonceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnonceResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody AnnonceRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(annonceService.updateAnnonce(id, dto, authentication.getPrincipal().toString()));
    }

    @PostMapping("/{id}/publier")
    public ResponseEntity<AnnonceResponseDTO> publier(
            @PathVariable String id,
            Authentication authentication) {
        return ResponseEntity.ok(annonceService.publierAnnonce(id, authentication.getPrincipal().toString()));
    }

    @PostMapping("/{id}/archiver")
    public ResponseEntity<AnnonceResponseDTO> archiver(
            @PathVariable String id,
            Authentication authentication) {
        return ResponseEntity.ok(annonceService.archiverAnnonce(id, authentication.getPrincipal().toString()));
    }

    @GetMapping("/vendeur/{vendeurId}")
    public ResponseEntity<List<AnnonceResponseDTO>> getByVendeur(
            @PathVariable String vendeurId) {
        return ResponseEntity.ok(annonceService.getAnnoncesByVendeurPublic(vendeurId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            Authentication authentication) {
        annonceService.deleteAnnonce(id, authentication.getPrincipal().toString());
        return ResponseEntity.noContent().build();
    }
}
