package com.doul.dealz.controllers;

import com.doul.dealz.model.Categorie;
import com.doul.dealz.model.dto.request.CategorieRequestDTO;
import com.doul.dealz.model.dto.response.CategorieResponseDTO;
import com.doul.dealz.services.CategorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    public ResponseEntity<List<CategorieResponseDTO>> getAll() {
        return ResponseEntity.ok(
                categorieService.getAllCategorie().stream()
                        .map(c -> new CategorieResponseDTO(c.getId(), c.getNom()))
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieResponseDTO> getOne(@PathVariable String id) {
        Categorie c = categorieService.getAnCategorie(id);
        return ResponseEntity.ok(new CategorieResponseDTO(c.getId(), c.getNom()));
    }

    @PostMapping
    public ResponseEntity<CategorieResponseDTO> create(@Valid @RequestBody CategorieRequestDTO dto) {
        Categorie c = categorieService.createCategorie(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CategorieResponseDTO(c.getId(), c.getNom()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody CategorieRequestDTO dto) {
        Categorie c = categorieService.updateCategorie(id, dto);
        return ResponseEntity.ok(new CategorieResponseDTO(c.getId(), c.getNom()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
}
