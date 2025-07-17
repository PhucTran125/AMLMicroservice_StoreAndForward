package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Data;

@Data
public class VerifyCustomerRequestDTO {
    private Long customerId;
    private String customerName;
    private String customerIdentificationNumber;
    private String dob;
    private String nationality;
    private String residentialAddress;
    private T24AMLResult.TaskType taskType;
}
