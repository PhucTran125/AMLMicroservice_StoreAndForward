package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Data;

@Data
public class T24AMLResult {
    public enum Status {
        CLEAR("CLEAR"),
        SUSPENDED("SUSPENDED"),
        SUSPICIOUS("SUSPICIOUS");

        Status(String status) {
            this.status = status;
        }

        private String status;
    }

    public enum TaskType {
        ACCOUNT_OPENING("ACCOUNT_OPENING"),
        TRANSACTION_TRANSFER("TRANSACTION_TRANSFER");

        TaskType(String type) {
            this.type = type;
        }

        private String type;
    }

    public enum ResultType {
        CUSTOMER_SCREENING_RESULT("CUSTOMER_SCREENING_RESULT"),
        TRANSACTION_MONITORING_RESULT("TRANSACTION_MONITORING_RESULT");

        ResultType(String type) {
            this.type = type;
        }

        private String type;
    }

    private TaskType taskType;
    private ResultType resultType; // CUSTOMER or TRANSACTION
    private Long id;
    private Status status;
    private String reason;
}
