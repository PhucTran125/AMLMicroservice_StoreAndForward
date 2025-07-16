package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Transaction {
    private Long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE; // Generate a positive long ID
    private Long timestamp;
    private Long amount;
    private String currency;
    private Long sourceAccountNumber;
    private Long destinationAccountNumber;
    private String customerId;
    private String customerName;
    private String customerIdentificationNumber;
    private LocalDateTime date;
    private String country;
    private String status;
}
