package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class AccountOpening {
    private UUID id = UUID.randomUUID();
    private Long timestamp;
    private String customerName;
    private String customerIdentificationNumber;
    private String dob;
    private String nationality;
    private String residentialAddress;
    private String status; // e.g., "pending", "approved", "rejected"
}
