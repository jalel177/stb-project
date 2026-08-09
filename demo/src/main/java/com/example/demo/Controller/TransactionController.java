package com.example.demo.Controller;

import com.example.demo.DTO.TransactionRequest;
import com.example.demo.DTO.TransactionResponse;
import com.example.demo.Entities.Transaction;
import com.example.demo.Service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/process")
    public ResponseEntity<TransactionResponse> process(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(transactionService.processTransaction(request, username));
    }

    @PostMapping("/validate")
    public ResponseEntity<TransactionResponse> validate(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(transactionService.validateTransaction(request, username));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactionHistory(username));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(Authentication authentication) {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Transaction>> getPendingTransactions() {
        return ResponseEntity.ok(transactionService.getPendingTransactions());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Transaction> approve(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(transactionService.approveTransaction(id, authentication.getName()));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Transaction> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(transactionService.rejectTransaction(id, reason, authentication.getName()));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<Transaction> getByReference(
            @PathVariable String reference,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactionByReference(reference, username));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getByAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountNumber, username));
    }
}