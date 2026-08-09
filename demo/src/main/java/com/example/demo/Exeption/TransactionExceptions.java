package com.example.demo.Exeption;

/**
 * Container class for all custom exceptions used in the application.
 */
public final class TransactionExceptions {

    private TransactionExceptions() {
        // Prevent instantiation
    }

    // ============================================================
    // ACCOUNT-RELATED EXCEPTIONS
    // ============================================================

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class AccountBlockedException extends RuntimeException {
        public AccountBlockedException(String message) {
            super(message);
        }
    }

    // ============================================================
    // TRANSACTION-RELATED EXCEPTIONS
    // ============================================================

    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }

    public static class DailyLimitExceededException extends RuntimeException {
        public DailyLimitExceededException(String message) {
            super(message);
        }
    }

    public static class TransactionNotFoundException extends RuntimeException {
        public TransactionNotFoundException(String message) {
            super(message);
        }
    }

    public static class FraudDetectedException extends RuntimeException {
        public FraudDetectedException(String message) {
            super(message);
        }
    }
    public static class InvalidTransactionStateException extends RuntimeException {
        public InvalidTransactionStateException(String message) {
            super(message);
        }
    }
    // ============================================================
    // AUTHORIZATION EXCEPTIONS
    // ============================================================

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}