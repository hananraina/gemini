package com.icebox.gemini.service;

import com.icebox.gemini.dto.AuthRequest;
import com.icebox.gemini.dto.AuthResponse;
import com.icebox.gemini.repository.RefreshTokenRepository;
import com.icebox.gemini.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository tokenRepository;

    public AuthResponse authenticate(AuthRequest authRequest) {
        var token = new UsernamePasswordAuthenticationToken(authRequest.getIdentifier(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        String accessToken = jwtTokenService.generateToken(authentication);
        Long expirationTime = jwtTokenService.extractExpirationTime(accessToken);
        // Extract principal
        Object principal = authentication.getPrincipal();
        String email = "";
        List<String> roles = new ArrayList<>();
        String refreshToken = "";

        if (principal instanceof SecurityUser securityUser) {
            email = securityUser.getUser().getEmail();
            roles = securityUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            refreshToken = refreshTokenService.generateRefreshToken(securityUser.getUser());
        }
        return new AuthResponse(accessToken, refreshToken, expirationTime, authentication.getName(), email, roles);
    }

    public AuthResponse refresh(String refreshToken, boolean rotateRefreshToken) {
        SecurityUser securityUser = refreshTokenService.getUserFromRefreshToken(refreshToken);
        if (securityUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );
        String accessToken = jwtTokenService.generateToken(authentication);
        Long expirationTime = jwtTokenService.extractExpirationTime(accessToken);

        // generate new refresh token
        String newRefreshToken = refreshToken;
        if (rotateRefreshToken) {
            refreshTokenService.deleteByRawToken(refreshToken);
            newRefreshToken = refreshTokenService.generateRefreshToken(securityUser.getUser());
        }

        String email = securityUser.getUser().getEmail();
        List<String> roles = securityUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponse(accessToken, newRefreshToken, expirationTime, authentication.getName(), email, roles);
    }
}
