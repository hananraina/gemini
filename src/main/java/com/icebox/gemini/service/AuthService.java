package com.icebox.gemini.service;

import com.icebox.gemini.dto.AuthRequest;
import com.icebox.gemini.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthResponse authenticate(AuthRequest authRequest) {
        var token = new UsernamePasswordAuthenticationToken(authRequest.getIdentifier(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        String jwtToken = jwtTokenService.generateToken(authentication);
        Long expirationTime = jwtTokenService.extractExpirationTime(jwtToken);
        return new AuthResponse(jwtToken, authentication.getName(), expirationTime);
    }
}
