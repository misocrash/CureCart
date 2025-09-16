package com.example.Meds.controller;


import com.example.Meds.repository.UsersRepository;
import com.example.Meds.model.User;
import com.example.Meds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        if (user == null) {
            return ResponseEntity.status(400).body("user not found");
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        boolean deleted = userService.deleteUser(userId);
        if (!deleted) {
            return ResponseEntity.status(404).body("User not foind");
        }
        return ResponseEntity.ok("User delted successfully");
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        String result = userService.registerUser(user);
        if (result.contains("already exists")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
