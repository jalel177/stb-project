package com.example.demo.Service;

import com.example.demo.DTO.UserSummary;
import com.example.demo.Enum.Role;
import com.example.demo.Entities.User;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;


    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getRole()))
                .toList();
    }
    public User updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return userRepository.save(user);
    }
}