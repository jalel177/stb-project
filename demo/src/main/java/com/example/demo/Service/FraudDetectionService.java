package com.example.demo.Service;


import com.example.demo.DTO.TransactionRequest;
import com.example.demo.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    private final TransactionRepository transactionRepository;

    public String checkForFraud(TransactionRequest request) {
        // Rule 1: Amount > 5000 TND
        if (request.getAmount().compareTo(new BigDecimal("5000")) > 0) {
            return "Amount exceeds 5000 TND (High Risk)";
        }

        // Rule 2: More than 3 transfers in 5 minutes
        LocalDateTime fiveMinAgo = LocalDateTime.now().minusMinutes(5);
        long count = transactionRepository.findBySenderOrReceiverAccount(request.getSenderAccount())
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(fiveMinAgo))
                .count();
        if (count >= 3) {
            return "More than 3 transfers in 5 minutes";
        }

        return null; // No fraud detected
    }
}
