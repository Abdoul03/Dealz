package com.doul.dealz.model.dto.response;

import com.doul.dealz.model.enums.TypeCompte;

public record UserResponseDTO(
        String id,
        String nom,
        String prenom,
        String telephone,
        String email,
        String password,
        TypeCompte typeCompte,
        boolean isPremiun,
        boolean isActive,
        float noteMoyenne,
        String localisation
) {
}
