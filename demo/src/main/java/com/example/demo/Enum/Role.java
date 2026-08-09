package com.example.demo.Enum;

public enum Role {
    CUSTOMER,
    EMPLOYEE,
    ADMIN;

    public record RoleUpdateRequest(Role role) {}}
