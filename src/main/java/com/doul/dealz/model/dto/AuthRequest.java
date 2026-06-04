package com.doul.dealz.model.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "Email ou numero de telephone invalide.") String identifiant,
        @NotBlank(message = "Mot de passe invalide.") String motDePasse
) {
}