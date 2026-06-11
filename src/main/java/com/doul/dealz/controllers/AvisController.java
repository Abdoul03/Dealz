package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.request.AvisRequestDTO;
import com.doul.dealz.model.dto.response.AvisResponseDTO;
import com.doul.dealz.services.AvisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("avis")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService;

    @PostMapping
    public ResponseEntity<AvisResponseDTO> create(
            @Valid @RequestBody AvisRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(avisService.createAvis(dto, authentication.getPrincipal().toString()));
    }

    @GetMapping("/recus")
    public ResponseEntity<List<AvisResponseDTO>> getAvisRecus(Authentication authentication) {
        return ResponseEntity.ok(avisService.getAvisRecus(authentication.getPrincipal().toString()));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<AvisResponseDTO>> getAvisByTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(avisService.getAvisByTransaction(transactionId));
    }
}
