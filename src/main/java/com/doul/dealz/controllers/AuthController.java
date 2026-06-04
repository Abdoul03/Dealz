package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.AuthRequest;
import com.doul.dealz.model.dto.request.UserRequestDTO;
import com.doul.dealz.model.dto.response.UserResponseDTO;
import com.doul.dealz.services.AuthService;
import com.doul.dealz.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthService.TokenPairResponse> login(@Valid @RequestBody AuthRequest authenticationRequest) {
        return ResponseEntity.ok(authService.authenticate(authenticationRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> deconnexion(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String result = authService.deleteRefreshToken(refreshToken);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthService.TokenPairResponse> refreshToken(@RequestBody Map<String, String> body) {
        // return ResponseEntity.ok(authentificationService.refresh(refreshToken));
        String refreshToken = body.get("refreshToken");
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

}
