package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.request.AdminRequestDTO;
import com.doul.dealz.model.dto.response.UserResponseDTO;
import com.doul.dealz.services.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Crée un nouvel administrateur.
     * Accessible uniquement aux administrateurs existants (ROLE_ADMIN).
     * POST /api/admin/admins
     */
    @PostMapping("/admins")
    public ResponseEntity<UserResponseDTO> createAdmin(
            @Valid @RequestBody AdminRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createAdmin(dto));
    }
}
