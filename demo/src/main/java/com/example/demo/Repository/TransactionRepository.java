package com.example.demo.Repository;

import com.example.demo.Entities.Transaction;
import com.example.demo.Enum.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReference(String reference);

    // Custom query: transactions where account is sender OR receiver
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount = :account OR t.receiverAccount = :account")
    List<Transaction> findBySenderOrReceiverAccount(@Param("account") String account);

    // Custom query: transactions where account is in a list of accounts (for history)
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount IN :accounts OR t.receiverAccount IN :accounts")
    List<Transaction> findBySenderOrReceiverAccountIn(@Param("accounts") List<String> accounts);

    // Sum of amount sent today for a given account
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.senderAccount = :accountNumber AND t.createdAt >= :startOfDay AND t.status = 'APPROVED'")
    BigDecimal sumAmountSentToday(@Param("accountNumber") String accountNumber,
                                  @Param("startOfDay") LocalDateTime startOfDay);

    // Count recent transactions (for fraud detection)
    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.senderAccount = :accountNumber AND t.createdAt >= :sinceTime")
    long countRecentTransactions(@Param("accountNumber") String accountNumber,
                                 @Param("sinceTime") LocalDateTime sinceTime);

    List<Transaction> findByStatus(TransactionStatus status);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
        WHERE t.senderAccount = :accountNumber
        AND t.status = 'APPROVED'
        AND t.createdAt >= :startOfDay
    """)
    BigDecimal sumApprovedAmountSentToday(@Param("accountNumber") String accountNumber,
                                          @Param("startOfDay") LocalDateTime startOfDay);}