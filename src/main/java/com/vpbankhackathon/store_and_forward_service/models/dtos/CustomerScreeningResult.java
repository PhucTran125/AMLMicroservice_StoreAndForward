package com.vpbankhackathon.store_and_forward_service.models.dtos;

import lombok.Data;

@Data
public class CustomerScreeningResult {
    public enum Status {
        CLEAR("CLEAR"),
        SUSPENDED("SUSPENDED"),
        SUSPICIOUS("SUSPICIOUS");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private Long customerId;
    private Status status;
    private String reason;
    private String requestId;
}
