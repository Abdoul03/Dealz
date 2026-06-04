package com.doul.dealz.repository;

import com.doul.dealz.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserIdAndToken(String userId, String token);
    Optional<RefreshToken> findByUserId(String id);
    Optional<RefreshToken> findByToken(String refreshToken);
}
