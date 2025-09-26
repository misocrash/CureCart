package com.example.Meds.controller;

import com.example.Meds.dto.UserDTO;
import com.example.Meds.dto.UserResponseDTO;
import com.example.Meds.entity.User;
import com.example.Meds.mapper.UserMapper;
import com.example.Meds.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    // 2. Accept UserDTO, return UserResponseDTO. Use @Valid.
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserDTO userDTO) {
        User registeredUser = userService.register(userDTO);
        // 3. Map the entity result to a response DTO before returning
        UserResponseDTO response = userMapper.toUserResponseDTO(registeredUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    // 4. Return UserResponseDTO
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toUserResponseDTO(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserDTO userDTO) {

        // The service now takes the DTO
        User updatedUserEntity = userService.updateProfile(id, userDTO);

        // Map the returned entity to a response DTO
        UserResponseDTO response = userMapper.toUserResponseDTO(updatedUserEntity);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> userList = userService.getAllUsers();

        // Use a stream to map each User entity in the list to a UserResponseDTO
        List<UserResponseDTO> responseList = userList.stream()
                .map(userMapper::toUserResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<?> deleteById(@PathVariable int id) {
//        return ResponseEntity.ok(userService.deleteById(id));
//    }
}
