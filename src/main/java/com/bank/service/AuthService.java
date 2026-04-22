package com.bank.service;

import com.bank.entity.Role;
import com.bank.entity.User;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(User user) {

        // ❌ Email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 🔥 IMPORTANT FIXES
        user.setPassword(encoder.encode(user.getPassword())); // encode password
        user.setRole(Role.USER); // set default role
        user.setKycStatus(false);

        return userRepository.save(user);
    }
}
