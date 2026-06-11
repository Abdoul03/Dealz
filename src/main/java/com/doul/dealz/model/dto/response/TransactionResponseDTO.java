package com.doul.dealz.model.dto.response;

import com.doul.dealz.model.enums.StatutTransaction;
import com.doul.dealz.model.enums.TypePaiement;

import java.time.LocalDateTime;

public record TransactionResponseDTO(
        String id,
        float montant,
        StatutTransaction statut,
        LocalDateTime dateTransaction,
        String modeRetrait,
        String annonceId,
        String annonceTitre,
        UserSummaryDTO acheteur,
        UserSummaryDTO vendeur,
        TypePaiement typePaiement,
        boolean paiementLibere
) {}
