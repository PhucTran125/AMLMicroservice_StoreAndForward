package com.vpbankhackathon.store_and_forward_service.pubsub.consumers;

import com.vpbankhackathon.store_and_forward_service.models.dtos.TransactionMonitoringResult;
import com.vpbankhackathon.store_and_forward_service.services.StoreAndForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionMonitoringResultConsumer {
    @Autowired
    StoreAndForwardService service;

    @KafkaListener(topics = "transaction-monitoring-result")
    public void consumeMessage(TransactionMonitoringResult result) {
        try {
            service.forwardTransactionMonitoringResult(result);
        } catch (Exception e) {
            System.err.println("Error in consumeMessage: " + e.getMessage());
            throw new RuntimeException("Failed to consume message from Kafka", e);
        }
    }
}
