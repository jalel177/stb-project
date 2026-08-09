package com.example.demo.DTO;



import com.example.demo.Enum.Role;

import java.math.BigDecimal;
import java.util.List;

public record UserDetails(
        Long id,
        String username,
        Role role,
        List<AccountSummaryDTO> accounts
) {
    public record AccountSummaryDTO(
            Long id,
            String accountNumber,
            BigDecimal balance,
            String status
    ) {}
}
