package com.vpbankhackathon.store_and_forward_service.repositories;

import com.vpbankhackathon.store_and_forward_service.models.entities.AMLRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface AMLRequestRepository extends JpaRepository<AMLRequest, UUID> {
    List<AMLRequest> findByStatusOrderByTimestampAsc(AMLRequest.RequestStatus status, PageRequest pageRequest);
}
