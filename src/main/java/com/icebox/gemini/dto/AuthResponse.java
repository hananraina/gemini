package com.icebox.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long expirationTime;
    private String username;
    private String email;
    private List<String> roles;
}
