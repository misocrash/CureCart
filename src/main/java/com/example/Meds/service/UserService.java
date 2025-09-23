package com.example.Meds.service;

import com.example.Meds.entity.User;
import java.util.List;

public interface UserService {
    User register(User user);
    User findById(Integer id);
    User updateProfile(Integer id, User updatedUser);
    List<User> getAllUsers();

    User deleteById(Integer id);
}