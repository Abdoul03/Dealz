package com.doul.dealz.model.dto.request;

import com.doul.dealz.model.enums.EtatArticle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AnnonceRequestDTO(
        @NotBlank(message = "Le titre est obligatoire.") String titre,
        @Size(max = 80, message = "La marque ne peut pas dépasser 80 caractères.") String marque,
        @Size(max = 40, message = "La taille ne peut pas dépasser 40 caractères.") String taille,
        @NotBlank(message = "La description est obligatoire.") String description,
        @NotNull(message = "Le prix est obligatoire.") @Positive(message = "Le prix doit être positif.") Float prix,
        String urlImage,
        List<String> urlImages,
        @NotNull(message = "L'état de l'article est obligatoire.") EtatArticle etat,
        @NotBlank(message = "La catégorie est obligatoire.") String categorieId,
        String quartier
) {}
