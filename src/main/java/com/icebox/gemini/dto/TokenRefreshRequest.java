package com.icebox.gemini.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
    private Boolean rotateRefreshToken = false;
}
