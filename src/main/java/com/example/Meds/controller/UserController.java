package com.example.Meds.controller;

import com.example.Meds.dto.SingleOrderDetailsDTO;
import com.example.Meds.dto.UserRegisterRequestDTO;
import com.example.Meds.entity.User;
import com.example.Meds.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    //autowire/di
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDTO request){
        userService.register(request);
        return new ResponseEntity<>("User registered!!", HttpStatus.CREATED);
    }

    //findallusers
    @GetMapping("/")
    public ResponseEntity<List<User>> findAllUsers(){
        List<User> userList= userService.findAllUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable int id){
        User user= userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable int id){
        userService.deleteById(id);
        return ResponseEntity.ok("User deleted!!");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id,@RequestBody User updatedUser){
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/orderHistory/{userId}")
    public ResponseEntity<?> getOrderHistory(@PathVariable int userId){
        List<SingleOrderDetailsDTO> response = userService.getOrderHistory(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
