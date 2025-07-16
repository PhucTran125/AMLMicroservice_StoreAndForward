package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Data;

@Data
public class TransactionMonitoringResult {
    private Long transactionId;
    private String status; // CLEAR or SUSPENDED or SUSPICIOUS
    private String reason;
}
