package com.doul.dealz.model.dto.response;

import com.doul.dealz.model.enums.StatutTransaction;

import java.time.LocalDateTime;

public record TransactionResponseDTO(
        String id,
        float montant,
        StatutTransaction statut,
        LocalDateTime dateTransaction,
        String modeRetrait,
        String annonceId,
        String annonceTitre,
        String annonceImageUrl,
        UserSummaryDTO acheteur,
        UserSummaryDTO vendeur,
        boolean acheteurConfirmeRemise,
        boolean vendeurConfirmeRemise
) {}
