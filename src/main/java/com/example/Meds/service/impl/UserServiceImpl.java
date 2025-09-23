package com.example.Meds.service.impl;

import com.example.Meds.entity.User;
import com.example.Meds.repository.UsersRepository;
import com.example.Meds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UsersRepository userRepository;

    @Autowired
    public UserServiceImpl(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User updateProfile(Integer id, User updatedUser) {
        User existing = findById(id);
        if (existing != null) {
            existing.setName(updatedUser.getName());
            existing.setEmail(updatedUser.getEmail());
            return userRepository.save(existing);
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
