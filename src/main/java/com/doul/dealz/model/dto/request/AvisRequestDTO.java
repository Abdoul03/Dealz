package com.doul.dealz.model.dto.request;

import com.doul.dealz.model.enums.TypeAvis;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AvisRequestDTO(
        @NotBlank(message = "La transaction est obligatoire.") String transactionId,
        @NotNull(message = "La note est obligatoire.")
        @Min(value = 1, message = "La note minimum est 1.")
        @Max(value = 5, message = "La note maximum est 5.") Integer note,
        String commentaire,
        @NotNull(message = "Le type d'avis est obligatoire.") TypeAvis typeAvis
) {}
