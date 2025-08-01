package com.icebox.gemini.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String identifier;
    private String password;
}
