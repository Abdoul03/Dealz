package com.doul.dealz.model.dto.response;

import com.doul.dealz.model.enums.EtatArticle;
import com.doul.dealz.model.enums.StatutAnnonce;

import java.time.LocalDateTime;
import java.util.List;

public record AnnonceResponseDTO(
        String id,
        String titre,
        String description,
        float prix,
        String urlImage,
        List<String> urlImages,
        EtatArticle etat,
        StatutAnnonce statut,
        LocalDateTime datePublication,
        boolean isBoosted,
        String pointRetrait,
        UserSummaryDTO vendeur,
        CategorieResponseDTO categorie
) {}
