package com.doul.dealz.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageRequestDTO(
        @NotBlank(message = "Le destinataire est obligatoire.") String destinataireId,
        @NotBlank(message = "L'annonce est obligatoire.") String annonceId,
        @NotBlank(message = "Le message ne peut pas être vide.") String contenu
) {}
