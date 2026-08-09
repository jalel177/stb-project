package com.example.demo.Repository;

import com.example.demo.Entities.Account;
import com.example.demo.Enum.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 1. Find account by account number (most common)
    Optional<Account> findByAccountNumber(String accountNumber);

    // 2. Check if account exists
    boolean existsByAccountNumber(String accountNumber);

    // 3. Find all accounts for a user (by username)
    List<Account> findByUser_Username(String username);

    // 4. Find accounts by status (ACTIVE, FROZEN, BLOCKED, CLOSED)
    List<Account> findByStatus(AccountStatus status);

    // 5. Find active accounts for a user
    @Query("SELECT a FROM Account a WHERE a.user.username = :username AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByUsername(@Param("username") String username);

    // 6. Get total balance for a user across all accounts
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.username = :username")
    BigDecimal getTotalBalanceForUser(@Param("username") String username);

    // 7. Find account by account number with user eager loaded (optimized)
    @Query("SELECT a FROM Account a JOIN FETCH a.user WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithUser(@Param("accountNumber") String accountNumber);

    // 8. Find all accounts with a specific status and balance below threshold (for monitoring)
    @Query("SELECT a FROM Account a WHERE a.status = :status AND a.balance < :threshold")
    List<Account> findByStatusAndBalanceLessThan(@Param("status") AccountStatus status,
                                                 @Param("threshold") BigDecimal threshold);

    // 9. Update account status (bulk operation)
    @Query("UPDATE Account a SET a.status = :status WHERE a.accountNumber = :accountNumber")
    void updateAccountStatus(@Param("accountNumber") String accountNumber,
                             @Param("status") AccountStatus status);

    // 10. Count total accounts by status
    long countByStatus(AccountStatus status);
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.senderAccount = :accountNumber " +
            "AND t.status = 'APPROVED' " +
            "AND t.createdAt >= :startOfDay")
    BigDecimal sumApprovedAmountSentToday(@Param("accountNumber") String accountNumber,
                                          @Param("startOfDay") LocalDateTime startOfDay);
}