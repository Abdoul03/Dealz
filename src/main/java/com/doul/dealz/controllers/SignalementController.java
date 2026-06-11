package com.doul.dealz.controllers;

import com.doul.dealz.model.Signalement;
import com.doul.dealz.model.dto.request.SignalementRequestDTO;
import com.doul.dealz.services.SignalementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("signalements")
@RequiredArgsConstructor
public class SignalementController {

    private final SignalementService signalementService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody SignalementRequestDTO dto,
            Authentication authentication) {
        signalementService.createSignalement(dto, authentication.getPrincipal().toString());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/mes-signalements")
    public ResponseEntity<List<Signalement>> getMes(Authentication authentication) {
        return ResponseEntity.ok(signalementService.getMesSignalements(authentication.getPrincipal().toString()));
    }
}
