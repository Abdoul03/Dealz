package com.doul.dealz.model.dto.request;

import com.doul.dealz.model.enums.TypePaiement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequestDTO(
        @NotBlank(message = "L'annonce est obligatoire.") String annonceId,
        @NotNull(message = "Le mode de paiement est obligatoire.") TypePaiement typePaiement,
        String modeRetrait
) {}
