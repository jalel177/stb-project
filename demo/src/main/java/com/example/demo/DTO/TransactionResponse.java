package com.example.demo.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private String status;
    private String transactionReference;
    private String reason;
}
