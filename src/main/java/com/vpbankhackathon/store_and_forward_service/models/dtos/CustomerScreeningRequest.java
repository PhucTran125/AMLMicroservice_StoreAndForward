package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerScreeningRequest {
    private String customerId;
    private String customerName;
    private String identificationNumber;
    private LocalDate dob;
    private String address;
    private String nationality;
    private String requestId;
}