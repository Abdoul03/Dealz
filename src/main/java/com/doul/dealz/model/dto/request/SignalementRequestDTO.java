package com.doul.dealz.model.dto.request;

import com.doul.dealz.model.enums.TypeSignalement;
import jakarta.validation.constraints.NotNull;

public record SignalementRequestDTO(
        @NotNull(message = "Le motif est obligatoire.") TypeSignalement motif,
        String description,
        String annonceId,
        String userCibleId
) {}
