package com.alltime.identity.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.alltime.identity.entity.RefreshToken;
import com.alltime.identity.entity.User;
import com.alltime.identity.repository.RefreshTokenRepository;
import com.alltime.identity.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createFreshToken(String phoneNumber) {
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("User matching number fot found"));

        // Clean out any existing tokens for this user before issuing a new one (Token
        // Rotation Strategy)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(2592000));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            refreshTokenRepository.flush(); // Flush here as well for safety
            throw new IllegalArgumentException("Refresh token has expired. Please execute full password re-login.");
        }
        return token;
    }

    @Transactional
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

}
