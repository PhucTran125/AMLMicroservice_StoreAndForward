package com.vpbankhackathon.store_and_forward_service.pubsub.consumers;

import com.vpbankhackathon.store_and_forward_service.models.dtos.CustomerScreeningResult;
import com.vpbankhackathon.store_and_forward_service.services.StoreAndForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerScreeningResultConsumer {
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    StoreAndForwardService service;

    @KafkaListener(topics = "customer-screening-result")
    public void consumeMessage(CustomerScreeningResult result) {
        try {
            if (result.getRequestId() == null) {
                System.err.println("Received null result from Kafka topic 'customer-screening-result'");
                return;
            }
            service.forwardCustomerScreeningResult(result);
        } catch (Exception e) {
            System.err.println("Error in consumeMessage: " + e.getMessage());
            throw new RuntimeException("Failed to consume message from Kafka", e);
        }
    }
}
