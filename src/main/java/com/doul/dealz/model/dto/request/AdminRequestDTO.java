package com.doul.dealz.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminRequestDTO(
        @NotBlank(message = "Le nom est obligatoire.") String nom,
        @NotBlank(message = "Le prénom est obligatoire.") String prenom,
        @NotBlank(message = "L'email est obligatoire.")
        @Email(message = "Format d'email invalide.") String email,
        @NotBlank(message = "Le téléphone est obligatoire.") String telephone,
        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.") String password,
        String niveau
) {}
