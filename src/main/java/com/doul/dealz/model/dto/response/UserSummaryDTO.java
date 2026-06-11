package com.doul.dealz.model.dto.response;

public record UserSummaryDTO(
        String id,
        String nom,
        String prenom,
        float noteMoyenne
) {}
