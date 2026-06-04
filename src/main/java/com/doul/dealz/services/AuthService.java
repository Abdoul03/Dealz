package com.doul.dealz.services;

import com.doul.dealz.config.JwtService;
import com.doul.dealz.model.RefreshToken;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.AuthRequest;
import com.doul.dealz.repository.RefreshTokenRepository;
import com.doul.dealz.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public TokenPairResponse authenticate(AuthRequest authenticationRequest) {
        boolean isEmail = EmailValidator.getInstance().isValid(authenticationRequest.identifiant());
        User userToAuthenticate ;

        if (isEmail){
            userToAuthenticate = userRepository.findByEmail(authenticationRequest.identifiant())
                    .orElseThrow(() -> new BadCredentialsException("Identifiant ou mot de passe incorrect."));
        }else {
            userToAuthenticate = userRepository.findByTelephone(authenticationRequest.identifiant())
                    .orElseThrow(() -> new BadCredentialsException("Identifiant ou mot de passe incorrect."));
        }

        if (BCrypt.checkpw(authenticationRequest.motDePasse(), userToAuthenticate.getPassword())) {
            String newAccessToken = jwtService.generateAccessToken(userToAuthenticate);
            String newRefreshToken = jwtService.generateRefreshToken(userToAuthenticate);
            storeRefreshToken(userToAuthenticate.getId(), newRefreshToken);
            return new TokenPairResponse(newAccessToken, newRefreshToken);
        }
        throw new BadCredentialsException(" Identifiant ou mot de passe incorrect.");
    }

    public String deleteRefreshToken(String refreshToken){
        if (!jwtService.isTokenRefreshValid(refreshToken)) {
            throw new JwtException("Invalid refresh token.");
        }

        String userId = jwtService.getUserIdFromToken(refreshToken);

        RefreshToken tokenEntity = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("Token introuvable ou déjà supprimé."));

        if (tokenEntity.getUser().getId() != userId) {
            throw new JwtException("Token ne correspond pas à l'utilisateur.");
        }

        refreshTokenRepository.delete(tokenEntity);
        return "Token supprimer avec succes";
    }

    @Transactional
    public TokenPairResponse refresh(String refreshToken) {
        if (!jwtService.isTokenRefreshValid(refreshToken)) {
            throw new JwtException("Invalid refresh token.");
        }
        String userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new JwtException("Utilisateur introuvable."));

        String hashedToken = hashToken(refreshToken);
        refreshTokenRepository.findByUserIdAndToken(user.getId(), hashedToken)
                .orElseThrow(() -> new JwtException("Refresh token introuvable."));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        storeRefreshToken(user.getId(), newRefreshToken);

        return new TokenPairResponse(newAccessToken, newRefreshToken);
    }

    private void storeRefreshToken(String userId, String refreshToken) {
        String hashedToken = hashToken(refreshToken);
        long expiryMs = jwtService.getRefreshTokenValidity();
        Instant expiresAt = Instant.now().plusMillis(expiryMs);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new JwtException("Utilisateur introuvable."));

        RefreshToken tokenToStore = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken(null, user, Instant.now(), null, ""));

        tokenToStore.setExpiresAt(expiresAt);
        tokenToStore.setToken(hashedToken);

        refreshTokenRepository.save(tokenToStore);
    }

    private String hashToken(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hash du token.", e);
        }
    }

    public Long getCurrentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

    public record TokenPairResponse(String accessToken, String refreshToken) {
    }
}
