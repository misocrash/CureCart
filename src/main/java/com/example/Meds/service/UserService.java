package com.example.Meds.service;
import com.example.Meds.dto.UserRegisterRequestDTO;
import com.example.Meds.entity.Role;
import com.example.Meds.repository.UsersRepository;
import com.example.Meds.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final CartService cartService;

    public UserService(UsersRepository userRepository,CartService cartService) {
        this.usersRepository = userRepository;
        this.cartService=cartService;
    }

    public void register(UserRegisterRequestDTO request) {

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.USER,
                LocalDateTime.now()
        );

        usersRepository.save(user);
        cartService.createCartForUser(user);

    }

    public List<User> findAllUsers() {
        return usersRepository.findAll();
    }

    public User findUserById(Integer id) {

        return usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteById(int id) {
        usersRepository.deleteById(id);
    }


    public User updateUser(int id, User updatedUser) {
        User optionalUser = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        optionalUser.setName(updatedUser.getName());
        optionalUser.setEmail(updatedUser.getEmail());
        optionalUser.setPassword(updatedUser.getPassword());

        return usersRepository.save(optionalUser);
    }
}