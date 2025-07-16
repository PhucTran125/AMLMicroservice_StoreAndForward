package com.vpbankhackathon.store_and_forward_service.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class AMLRequest {
    public enum RequestStatus {
        PENDING("PENDING"),
        SENT("SENT"),
        ACKED("ACKED");

        RequestStatus(String pending) {}
    }

    @Id
    private UUID id = UUID.randomUUID();
    private Long timestamp = Instant.now().toEpochMilli();
    private RequestStatus status;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String data;
}