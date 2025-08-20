package com.icebox.gemini.service;

import com.icebox.gemini.entity.RefreshToken;
import com.icebox.gemini.entity.User;
import com.icebox.gemini.repository.RefreshTokenRepository;
import com.icebox.gemini.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {
    @Value("${jwt.refreshTtl:}")
    private long refreshTtl;

    private RefreshTokenRepository tokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    public String generateRefreshToken(User user) {
        String refreshToken = generateSecureToken();
        String tokenHash = getHash(refreshToken);

        // save to db
        RefreshToken token = new RefreshToken();
        token.setTokenHash(tokenHash);
        token.setUser(user);
        token.setSessionId(""); // TODO
        token.setExpiresAt(Instant.now().plusSeconds(refreshTtl));
        token.setCreatedAt(Instant.now());

        tokenRepository.save(token);
        return refreshToken;
    }
    public boolean isValid(String refreshToken) {
        String tokenHash = getHash(refreshToken);
        return tokenRepository.findByTokenHash(tokenHash)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now())).isPresent();
    }
    public SecurityUser getUserFromRefreshToken(String refreshToken) {
        String tokenHash = getHash(refreshToken);
        return tokenRepository.findByTokenHash(tokenHash).map(RefreshToken::getUser).map(SecurityUser::new).orElse(null);
    }
// todo handle exceptions
    @Transactional
    public void deleteByRawToken(String rawToken) {
        String tokenHash = getHash(rawToken);
        tokenRepository.deleteByTokenHash(tokenHash);
    }

    public void deleteAllByUser(User user) {
        tokenRepository.deleteByUser(user);
    }
    @Transactional
    public void deleteByUserId(Long userId) {
        tokenRepository.deleteByUser_Id(userId);
    }

    public void deleteExpiredTokens() {
        tokenRepository.deleteAllExpired(); // assuming custom @Query
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String getHash(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
