package com.icebox.gemini.controller;

import com.icebox.gemini.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private TestService testService;
    @RequestMapping("/api/secured")
    public String test() {
        return testService.test();
    }

    @RequestMapping("/api/user-info")
    public String userInfo(Authentication authentication) {
        return "User: " + authentication.getName() + ", Roles: " + authentication.getAuthorities().toString();
    }
}
