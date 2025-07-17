package com.vpbankhackathon.store_and_forward_service.pubsub.consumers;

import com.vpbankhackathon.store_and_forward_service.models.entities.AMLRequest;
import com.vpbankhackathon.store_and_forward_service.repositories.AMLRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RequestAckConsumer {

    @Autowired
    AMLRequestRepository amlRequestRepository;

    @KafkaListener(topics = "cs-acks", groupId = "sf-acks-group")
    public void handleAck(String requestId) {
        AMLRequest tx = amlRequestRepository.findById(requestId).orElse(null);
        if (tx != null) {
            tx.setStatus(AMLRequest.RequestStatus.ACKED);
            amlRequestRepository.save(tx);
        }
    }
}
