package com.vpbankhackathon.store_and_forward_service.models.entities;

import com.vpbankhackathon.store_and_forward_service.models.dtos.T24AMLResult;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "aml_requests")
@Data
public class AMLRequest {
    public enum RequestStatus {
        PENDING("PENDING"),
        SENT("SENT"),
        ACKED("ACKED");

        RequestStatus(String pending) {}
    }

    @Id
    private String id;
    private Long timestamp = Instant.now().toEpochMilli();
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String data;
    @Column(name = "request_id")
    private String requestId;
    private T24AMLResult.TaskType taskType;
}