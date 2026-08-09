package com.example.demo.DTO;

import com.example.demo.Enum.Role;

public record UserSummary(Long id, String username, Role role) {}
