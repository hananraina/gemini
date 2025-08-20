package com.icebox.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long expirationTime;
    private String username;
    private Long id;
    private List<String> roles;
}
