package com.judahharris.kohack2025.auth;

import com.judahharris.kohack2025.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .map(user -> PasswordUtil.matches(password, user.getHashedPassword()))
            .orElse(false);
    }

    public boolean doesUserExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(username, hashedPassword);
        userRepository.save(user);
        return user;
    }
}
