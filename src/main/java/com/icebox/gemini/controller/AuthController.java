package com.icebox.gemini.controller;

import com.icebox.gemini.dto.AuthRequest;
import com.icebox.gemini.dto.AuthResponse;
import com.icebox.gemini.dto.TokenRefreshRequest;
import com.icebox.gemini.security.SecurityUser;
import com.icebox.gemini.service.AuthService;
import com.icebox.gemini.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        Boolean rotateRefreshToken = request.getRotateRefreshToken();
        if (refreshTokenService.isValid(refreshToken)) {
            return authService.refresh(refreshToken, rotateRefreshToken);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("id");
        if(userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok("Logout successful");
    }
}
