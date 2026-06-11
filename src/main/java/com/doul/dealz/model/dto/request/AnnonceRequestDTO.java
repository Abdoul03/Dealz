package com.doul.dealz.model.dto.request;

import com.doul.dealz.model.enums.EtatArticle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record AnnonceRequestDTO(
        @NotBlank(message = "Le titre est obligatoire.") String titre,
        @NotBlank(message = "La description est obligatoire.") String description,
        @NotNull(message = "Le prix est obligatoire.") @Positive(message = "Le prix doit être positif.") Float prix,
        String urlImage,
        List<String> urlImages,
        @NotNull(message = "L'état de l'article est obligatoire.") EtatArticle etat,
        @NotBlank(message = "La catégorie est obligatoire.") String categorieId,
        String pointRetrait
) {}
