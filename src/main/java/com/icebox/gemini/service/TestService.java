package com.icebox.gemini.service;

import com.icebox.gemini.entity.User;
import com.icebox.gemini.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestService {
    @Autowired
    private UserRepository userRepository;
    public String test() {
        Optional<User> user = userRepository.findByUsername("hanan");
        return user.map(User::getEmail).orElse("null");
    }

}
