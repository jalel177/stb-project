package com.example.demo.Service;

import com.example.demo.DTO.TransactionRequest;
import com.example.demo.DTO.TransactionResponse;
import com.example.demo.Entities.Transaction;
import com.example.demo.Entities.Account;
import com.example.demo.Enum.TransactionStatus;
import com.example.demo.Enum.AccountStatus;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.TransactionRepository;
import com.example.demo.Exeption.TransactionExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudService;

    // 1. Submit transaction — creates a PENDING record, does NOT move money
    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request, String username) {
        log.info("Submitting transaction for user: {}", username);

        Account sender = validateSenderAccount(request.getSenderAccount(), username);
        Account receiver = validateReceiverAccount(request.getReceiverAccount());

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new TransactionExceptions.InsufficientBalanceException(
                    "Insufficient balance. Available: " + sender.getBalance()
            );
        }

        checkDailyLimit(sender, request.getAmount());

        String fraudAlert = fraudService.checkForFraud(request);
        if (fraudAlert != null) {
            log.warn("Fraud detected: {}", fraudAlert);
            throw new TransactionExceptions.FraudDetectedException("Fraud detected: " + fraudAlert);
        }

        Transaction transaction = new Transaction();
        transaction.setReference("TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transaction.setSenderAccount(request.getSenderAccount());
        transaction.setReceiverAccount(request.getReceiverAccount());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Transaction {} submitted, awaiting approval", transaction.getReference());
        return new TransactionResponse("PENDING", transaction.getReference(), "Awaiting admin approval");
    }

    // 2. Validate transaction (pre-check, no state change)
    public TransactionResponse validateTransaction(TransactionRequest request, String username) {
        log.info("Validating transaction for user: {}", username);

        Account sender = validateSenderAccount(request.getSenderAccount(), username);
        Account receiver = validateReceiverAccount(request.getReceiverAccount());

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            return new TransactionResponse("REJECTED", null, "Insufficient balance");
        }

        try {
            checkDailyLimit(sender, request.getAmount());
        } catch (TransactionExceptions.DailyLimitExceededException e) {
            return new TransactionResponse("REJECTED", null, e.getMessage());
        }

        String fraudAlert = fraudService.checkForFraud(request);
        if (fraudAlert != null) {
            return new TransactionResponse("REJECTED", null, "Fraud detected: " + fraudAlert);
        }

        return new TransactionResponse("VALIDATED", null, "Transaction is valid");
    }

    // 3. Admin: approve a pending transaction — money actually moves here
    @Transactional
    public Transaction approveTransaction(Long id, String adminUsername) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionExceptions.TransactionNotFoundException(
                        "Transaction not found: " + id
                ));

        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new TransactionExceptions.InvalidTransactionStateException(
                    "Only PENDING transactions can be approved. Current status: " + tx.getStatus()
            );
        }

        Account sender = accountRepository.findByAccountNumber(tx.getSenderAccount())
                .orElseThrow(() -> new TransactionExceptions.AccountNotFoundException(
                        "Sender account not found: " + tx.getSenderAccount()
                ));
        Account receiver = accountRepository.findByAccountNumber(tx.getReceiverAccount())
                .orElseThrow(() -> new TransactionExceptions.AccountNotFoundException(
                        "Receiver account not found: " + tx.getReceiverAccount()
                ));

        if (sender.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionExceptions.AccountBlockedException(
                    "Sender account is no longer active: " + sender.getStatus()
            );
        }
        if (receiver.getStatus() == AccountStatus.BLOCKED || receiver.getStatus() == AccountStatus.CLOSED) {
            throw new TransactionExceptions.AccountBlockedException(
                    "Receiver account can no longer accept funds: " + receiver.getStatus()
            );
        }
        if (sender.getBalance().compareTo(tx.getAmount()) < 0) {
            throw new TransactionExceptions.InsufficientBalanceException(
                    "Insufficient balance at approval time. Available: " + sender.getBalance()
            );
        }

        sender.setBalance(sender.getBalance().subtract(tx.getAmount()));
        receiver.setBalance(receiver.getBalance().add(tx.getAmount()));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        tx.setStatus(TransactionStatus.APPROVED);
        transactionRepository.save(tx);

        log.info("Transaction {} approved by admin {}", tx.getReference(), adminUsername);
        return tx;
    }

    // 4. Admin: reject a pending transaction — nothing moves
    @Transactional
    public Transaction rejectTransaction(Long id, String reason, String adminUsername) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionExceptions.TransactionNotFoundException(
                        "Transaction not found: " + id
                ));

        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new TransactionExceptions.InvalidTransactionStateException(
                    "Only PENDING transactions can be rejected. Current status: " + tx.getStatus()
            );
        }

        tx.setStatus(TransactionStatus.REJECTED);
        tx.setRejectionReason(reason != null ? reason : "No reason provided");
        transactionRepository.save(tx);

        log.info("Transaction {} rejected by admin {}", tx.getReference(), adminUsername);
        return tx;
    }

    // 5. Admin: list pending transactions awaiting review
    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.PENDING);
    }

    // 6. Get transaction history for a user (all accounts)
    public List<Transaction> getTransactionHistory(String username) {
        List<Account> accounts = accountRepository.findByUser_Username(username);
        List<String> accountNumbers = accounts.stream()
                .map(Account::getAccountNumber)
                .toList();
        return transactionRepository.findBySenderOrReceiverAccountIn(accountNumbers);
    }

    // 7. Get transaction by reference
    public Transaction getTransactionByReference(String reference, String username) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new TransactionExceptions.TransactionNotFoundException(
                        "Transaction not found: " + reference
                ));

        List<Account> accounts = accountRepository.findByUser_Username(username);
        boolean isAuthorized = accounts.stream()
                .anyMatch(a -> a.getAccountNumber().equals(transaction.getSenderAccount()) ||
                        a.getAccountNumber().equals(transaction.getReceiverAccount()));

        if (!isAuthorized) {
            throw new TransactionExceptions.UnauthorizedException(
                    "You are not authorized to view this transaction"
            );
        }
        return transaction;
    }

    // 8. Get all transactions (ADMIN only)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // 9. Get transactions by a specific account (user must own it)
    public List<Transaction> getTransactionsByAccount(String accountNumber, String username) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionExceptions.AccountNotFoundException(
                        "Account not found: " + accountNumber
                ));

        if (!account.getUser().getUsername().equals(username)) {
            throw new TransactionExceptions.UnauthorizedException(
                    "You are not authorized to view this account's transactions"
            );
        }

        return transactionRepository.findBySenderOrReceiverAccount(accountNumber);
    }

    // ============ Helper Methods ============

    private Account validateSenderAccount(String accountNumber, String username) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionExceptions.AccountNotFoundException(
                        "Sender account not found: " + accountNumber
                ));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionExceptions.AccountBlockedException(
                    "Sender account is " + account.getStatus() + " — cannot send funds"
            );
        }

        if (!account.getUser().getUsername().equals(username)) {
            throw new TransactionExceptions.UnauthorizedException(
                    "You are not authorized to use this account"
            );
        }
        return account;
    }

    private Account validateReceiverAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionExceptions.AccountNotFoundException(
                        "Receiver account not found: " + accountNumber
                ));

        // FROZEN accounts can still receive — only BLOCKED/CLOSED are fully locked
        if (account.getStatus() == AccountStatus.BLOCKED || account.getStatus() == AccountStatus.CLOSED) {
            throw new TransactionExceptions.AccountBlockedException(
                    "Receiver account is " + account.getStatus() + " — cannot accept funds"
            );
        }
        return account;
    }

    private void checkDailyLimit(Account account, BigDecimal amount) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        BigDecimal totalSentToday = transactionRepository.sumApprovedAmountSentToday(
                account.getAccountNumber(), startOfDay
        );

        if (totalSentToday == null) totalSentToday = BigDecimal.ZERO;

        if (totalSentToday.add(amount).compareTo(account.getDailyLimit()) > 0) {
            throw new TransactionExceptions.DailyLimitExceededException(
                    "Daily limit exceeded. Limit: " + account.getDailyLimit() +
                            ", Already sent: " + totalSentToday + ", Attempting: " + amount
            );
        }
    }
}