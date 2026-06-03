package com.doul.dealz.config;

import com.doul.dealz.model.Administrateur;
import com.doul.dealz.model.User;
import com.doul.dealz.model.enums.TypeCompte;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String jwtSecret;
    private SecretKey secretKey;

    private final long accessTokenValidity  = 15L * 60L * 1000L;               // 15 min
    private final long refreshTokenValidity = 30L * 24L * 60L * 60L * 1000L;   // 30 jours

    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtSecret.getBytes(StandardCharsets.UTF_8))
        );
    }

    // ── Génération ────────────────────────────────────────────────

    /**
     * Méthode privée centrale.
     * On passe directement l'objet User pour en extraire tous les claims Dealz.
     */
    private String generateToken(User user, TokenType tokenType, long expireAt) {
        Date now        = Date.from(Instant.now());
        Date expiration = new Date(now.getTime() + expireAt);

        return Jwts.builder()
                .subject(user.getId().toString())           // sub = userId (Long → String)
                .claim("type",        tokenType.name())
                // ── Claims Dealz ──────────────────────────────────
                .claim("typeCompte",  user.getTypeCompte().name())  // NORMAL | COMMERCANT
                .claim("isPremium",   user.isPremium())              // accès boost/visibilité
                .claim("isAdmin",     user instanceof Administrateur) // true = accès back-office
                // ─────────────────────────────────────────────────
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, TokenType.ACCESS_TOKEN, accessTokenValidity);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, TokenType.REFRESH_TOKEN, refreshTokenValidity);
    }

    // ── Validation ────────────────────────────────────────────────

    public boolean isTokenAccessValid(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) return false;
        return TokenType.ACCESS_TOKEN.name().equals(claims.get("type"));
    }

    public boolean isTokenRefreshValid(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) return false;
        return TokenType.REFRESH_TOKEN.name().equals(claims.get("type"));
    }

    // ── Extraction ────────────────────────────────────────────────

    public String getUserIdFromToken(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) throw new IllegalArgumentException("Token invalide.");
        return claims.getSubject();
    }

    /**
     * Extrait le TypeCompte (NORMAL | COMMERCANT).
     * Utilisé dans le filtre pour construire les GrantedAuthority Spring Security.
     */
    public TypeCompte getTypeCompteFromToken(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) throw new IllegalArgumentException("Token invalide.");
        Object val = claims.get("typeCompte");
        if (val == null) throw new IllegalArgumentException("typeCompte absent du token.");
        return TypeCompte.valueOf(val.toString());
    }

    /**
     * Indique si l'utilisateur est administrateur.
     * Utilisé dans le filtre pour ajouter ROLE_ADMIN aux authorities.
     */
    public boolean getIsAdminFromToken(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) return false;
        Object val = claims.get("isAdmin");
        return Boolean.TRUE.equals(val);
    }

    public boolean getIsPremiumFromToken(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) return false;
        Object val = claims.get("isPremium");
        return Boolean.TRUE.equals(val);
    }

    // ── Parsing interne ───────────────────────────────────────────

    private Claims parseAllClaims(String token) {
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .getPayload();
    }

    // ── Enum interne ──────────────────────────────────────────────

    private enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }
}