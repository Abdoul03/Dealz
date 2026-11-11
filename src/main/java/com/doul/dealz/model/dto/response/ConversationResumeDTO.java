package com.doul.dealz.model.dto.response;

import java.time.LocalDateTime;

public record ConversationResumeDTO(
        UserSummaryDTO interlocuteur,
        String annonceId,
        String annonceTitre,
        String annonceImageUrl,
        String dernierMessage,
        LocalDateTime dateEnvoi,
        boolean hasUnread
) {}
