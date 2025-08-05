package com.icebox.gemini.service;

import com.icebox.gemini.dto.AuthRequest;
import com.icebox.gemini.dto.AuthResponse;
import com.icebox.gemini.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // Extract principal
        Object principal = authentication.getPrincipal();
        String email = "";
        List<String> roles = new ArrayList<>();

        if (principal instanceof SecurityUser securityUser) {
            email = securityUser.getUser().getEmail();
            roles = securityUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return new AuthResponse(jwtToken, expirationTime, authentication.getName(), email, roles);
    }
}
