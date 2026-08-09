package com.example.demo.Entities;


import com.example.demo.Enum.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reference;
    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
    private String rejectionReason;
    private LocalDateTime createdAt = LocalDateTime.now();


}