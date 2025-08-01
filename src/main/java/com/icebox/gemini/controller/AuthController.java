package com.icebox.gemini.controller;

import com.icebox.gemini.dto.AuthRequest;
import com.icebox.gemini.dto.AuthResponse;
import com.icebox.gemini.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/token")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }
}
