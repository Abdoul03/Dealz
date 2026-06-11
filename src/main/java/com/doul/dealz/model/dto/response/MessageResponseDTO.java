package com.doul.dealz.model.dto.response;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        String id,
        String contenu,
        LocalDateTime dateEnvoi,
        boolean isRead,
        UserSummaryDTO expediteur,
        UserSummaryDTO destinataire,
        String annonceId
) {}
