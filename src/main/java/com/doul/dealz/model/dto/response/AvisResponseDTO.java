package com.doul.dealz.model.dto.response;

import com.doul.dealz.model.enums.TypeAvis;

import java.time.LocalDateTime;

public record AvisResponseDTO(
        String id,
        int note,
        String commentaire,
        LocalDateTime dateAvis,
        TypeAvis typeAvis,
        UserSummaryDTO auteur,
        UserSummaryDTO cible
) {}
