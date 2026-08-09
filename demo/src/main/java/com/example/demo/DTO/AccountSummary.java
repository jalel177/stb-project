package com.example.demo.DTO;

import com.example.demo.Enum.AccountStatus;

import java.math.BigDecimal;

public record AccountSummary (Long id, String accountNumber, String ownerName, BigDecimal balance, AccountStatus status) {}