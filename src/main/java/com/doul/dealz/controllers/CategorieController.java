package com.doul.dealz.controllers;

import com.doul.dealz.model.Categorie;
import com.doul.dealz.model.dto.request.CategorieRequestDTO;
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
    public ResponseEntity<List<Categorie>> getAll() {
        return ResponseEntity.ok(categorieService.getAllCategorie());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getOne(@PathVariable String id) {
        return ResponseEntity.ok(categorieService.getAnCategorie(id));
    }

    @PostMapping
    public ResponseEntity<Categorie> create(@Valid @RequestBody CategorieRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.createCategorie(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categorie> update(
            @PathVariable String id,
            @Valid @RequestBody CategorieRequestDTO dto) {
        return ResponseEntity.ok(categorieService.updateCategorie(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
}
