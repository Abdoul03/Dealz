package com.doul.dealz.model.dto.request;

public record UserRequestDTO(
        String nom,
        String prenom,
        String telephone,
        String email,
        String password
) {
}
