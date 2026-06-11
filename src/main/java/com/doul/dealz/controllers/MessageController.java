package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.request.MessageRequestDTO;
import com.doul.dealz.model.dto.response.ConversationResumeDTO;
import com.doul.dealz.model.dto.response.MessageResponseDTO;
import com.doul.dealz.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> send(
            @Valid @RequestBody MessageRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(dto, authentication.getPrincipal().toString()));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResumeDTO>> getConversations(Authentication authentication) {
        return ResponseEntity.ok(messageService.getConversationsResume(authentication.getPrincipal().toString()));
    }

    @GetMapping("/conversation/{autreUserId}/{annonceId}")
    public ResponseEntity<List<MessageResponseDTO>> getConversation(
            @PathVariable String autreUserId,
            @PathVariable String annonceId,
            Authentication authentication) {
        return ResponseEntity.ok(messageService.getConversation(
                autreUserId, annonceId, authentication.getPrincipal().toString()));
    }

    @PatchMapping("/conversation/{autreUserId}/{annonceId}/lire")
    public ResponseEntity<Void> marquerLu(
            @PathVariable String autreUserId,
            @PathVariable String annonceId,
            Authentication authentication) {
        messageService.marquerConversationLue(autreUserId, annonceId, authentication.getPrincipal().toString());
        return ResponseEntity.noContent().build();
    }
}
