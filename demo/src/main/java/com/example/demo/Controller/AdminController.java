package com.example.demo.Controller;

import com.example.demo.DTO.AccountSummary;
import com.example.demo.DTO.UserSummary;
import com.example.demo.Entities.Account;
import com.example.demo.Entities.Transaction;
import com.example.demo.Enum.AccountStatus;
import com.example.demo.Enum.Role;
import com.example.demo.Entities.User;

import com.example.demo.Enum.TransactionStatus;
import com.example.demo.Exeption.TransactionExceptions;
import com.example.demo.Service.AccountService;
import com.example.demo.Service.AdminService;
import com.example.demo.Service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> listUsers() {
        return ResponseEntity.ok(adminService.listUsers());
    }
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest body) {
        try {
            User updated = adminService.updateUserRole(id, body.role());
            return ResponseEntity.ok(Map.of(
                    "message", "Role updated",
                    "username", updated.getUsername(),
                    "role", updated.getRole()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
    @PutMapping("/accounts/{accountNumber}/status")
    public ResponseEntity<?> updateAccountStatus(
            @PathVariable String accountNumber,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        AccountStatus status = AccountStatus.valueOf(body.get("status").toUpperCase());
        Account updated = accountService.updateAccountStatus(accountNumber, status, authentication.getName());
        return ResponseEntity.ok(Map.of(
                "accountNumber", updated.getAccountNumber(),
                "status", updated.getStatus()
        ));
    }

    @GetMapping("/transactions/pending")
    public ResponseEntity<List<Transaction>> getPendingTransactions() {
        return ResponseEntity.ok(transactionService.getPendingTransactions());
    }

    @PutMapping("/transactions/{id}/approve")
    public ResponseEntity<Transaction> approve(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(transactionService.approveTransaction(id, authentication.getName()));
    }

    @PutMapping("/transactions/{id}/reject")
    public ResponseEntity<Transaction> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {

        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(transactionService.rejectTransaction(id, reason, authentication.getName()));
    }
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountSummary>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
}
