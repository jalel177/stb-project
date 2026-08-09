package com.example.demo.Service;

import com.example.demo.DTO.AccountSummary;
import com.example.demo.Entities.Account;
import com.example.demo.Enum.AccountStatus;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Exeption.TransactionExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account updateAccountStatus(String accountNumber, AccountStatus newStatus, String adminUsername) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionExceptions.AccountNotFoundException(
                        "Account not found: " + accountNumber
                ));

        AccountStatus oldStatus = account.getStatus();
        account.setStatus(newStatus);
        accountRepository.save(account);

        log.info("Account {} status changed {} -> {} by admin {}",
                accountNumber, oldStatus, newStatus, adminUsername);

        return account;
    }



    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionExceptions.AccountNotFoundException(
                        "Account not found: " + accountNumber
                ));
    }
    public List<AccountSummary> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(a -> new AccountSummary(a.getId(), a.getAccountNumber(), a.getOwnerName(), a.getBalance(), a.getStatus()))
                .toList();
    }
}