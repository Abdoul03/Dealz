package com.doul.dealz.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TransactionRequestDTO(
        @NotBlank(message = "L'annonce est obligatoire.") String annonceId
) {}
