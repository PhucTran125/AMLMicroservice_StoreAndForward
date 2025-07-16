package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Data;

@Data
public class CustomerScreeningResult {
    private Long customerId;
    private String status; // CLEAR or SUSPENDED or SUSPICIOUS
    private String reason;
}
