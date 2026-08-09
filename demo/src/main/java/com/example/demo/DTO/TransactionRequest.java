package com.example.demo.DTO;


import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotBlank private String senderAccount;
    @NotBlank private String receiverAccount;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
}
