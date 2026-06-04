package com.doul.dealz.controllers;

import com.doul.dealz.model.dto.request.UserRequestDTO;
import com.doul.dealz.model.dto.response.UserResponseDTO;
import com.doul.dealz.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getAnUser(@PathVariable String id) {
        UserResponseDTO user = userService.getAnUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.supprimeUser(id);
        return ResponseEntity.noContent().build();
    }
}
