package com.example.demo.Controller;


import com.example.demo.DTO.AuthRequest;
import com.example.demo.DTO.AuthResponse;
import com.example.demo.DTO.SignupRequest;
import com.example.demo.DTO.UserDetails;
import com.example.demo.Entities.User;
import com.example.demo.Service.AuthService;
import com.example.demo.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDetails> getOwnProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getOwnProfile(username));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {
        System.out.println("Received signup request: " + request);
        try {
            User newUser = authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("message", "User created successfully", "username", newUser.getUsername())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
