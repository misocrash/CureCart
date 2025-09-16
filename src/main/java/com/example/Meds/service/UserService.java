package com.example.Meds.service;

import com.example.Meds.model.User;
import com.example.Meds.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;

    // admin only
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    // FINDING THE USER
    public User findById(Integer id) {
        return usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // REGISTRATION
    public String registerUser(User user) {
        Optional<User> existing = usersRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            return "Email already exists";
        }
        user.setCreatedAt(LocalDateTime.now());
        usersRepository.save(user);
        return "User registered successfully";
    }

    // AUTHENTICATION
    public User authenticate(String email, String rawPassword) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!user.getPassword().equals(rawPassword)) {
            throw new RuntimeException("Invalid creds");
        }
        return user;
    }

    // UPDATING TEH USER
    public User updateUser(int id, User updatedUser) {
        Optional<User> optionalUser = usersRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        user.setRole(updatedUser.getRole());
        return usersRepository.save(user);
    }

    // DELETE THE USER
    public boolean deleteUser(int id) {
        Optional<User> optionalUser = usersRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return false;
        }
        usersRepository.deleteById(id);
        return true;
    }
}
