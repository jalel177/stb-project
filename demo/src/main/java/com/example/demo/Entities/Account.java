package com.example.demo.Entities;


import com.example.demo.Enum.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)  // This is important!
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal dailyLimit = new BigDecimal("10000");
}
