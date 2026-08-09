package com.example.demo.Exeption;

import com.example.demo.Exeption.TransactionExceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== ACCOUNT EXCEPTIONS ==========
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleAccountNotFound(AccountNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<?> handleAccountBlocked(AccountBlockedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ========== TRANSACTION EXCEPTIONS ==========
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> handleInsufficientBalance(InsufficientBalanceException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<?> handleDailyLimit(DailyLimitExceededException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<?> handleTransactionNotFound(TransactionNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(FraudDetectedException.class)
    public ResponseEntity<?> handleFraud(FraudDetectedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ========== AUTHORIZATION EXCEPTION ==========
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ========== VALIDATION EXCEPTION ==========
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
    }

    // ========== FALLBACK ==========
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error: " + ex.getMessage());
    }

    // ========== HELPER ==========
    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}