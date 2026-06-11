package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.request.TransactionRequestDTO;
import com.doul.dealz.model.dto.response.TransactionResponseDTO;
import com.doul.dealz.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> initier(
            @Valid @RequestBody TransactionRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.initierTransaction(dto, authentication.getPrincipal().toString()));
    }

    @PostMapping("/{id}/confirmer")
    public ResponseEntity<TransactionResponseDTO> confirmer(
            @PathVariable String id,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.confirmerTransaction(id, authentication.getPrincipal().toString()));
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<TransactionResponseDTO> annuler(
            @PathVariable String id,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.annulerTransaction(id, authentication.getPrincipal().toString()));
    }

    @GetMapping("/achats")
    public ResponseEntity<List<TransactionResponseDTO>> getMesAchats(Authentication authentication) {
        return ResponseEntity.ok(transactionService.getMesAchats(authentication.getPrincipal().toString()));
    }

    @GetMapping("/ventes")
    public ResponseEntity<List<TransactionResponseDTO>> getMesVentes(Authentication authentication) {
        return ResponseEntity.ok(transactionService.getMesVentes(authentication.getPrincipal().toString()));
    }
}
