package com.example.Meds.service;

import com.example.Meds.model.User;
import com.example.Meds.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UsersRepository usersRepository;

    public String login(User loginUser) {
        Optional<User> optionalUser = usersRepository.findByEmail(loginUser.getEmail());

        if (optionalUser.isEmpty()) {
            return "Invalid email or password";
        }

        User user = optionalUser.get();
        System.out.println("User Data: " + user);
        System.out.println("Login User data: " + loginUser);
        System.out.println("User " + user.getPassword());;
        System.out.println("Login User " + loginUser.getPassword());
        if (!user.getPassword().equals(loginUser.getPassword())) {
            return "invalid email and password";
        }

        return "Login successful for " + user.getName();
    }
}
