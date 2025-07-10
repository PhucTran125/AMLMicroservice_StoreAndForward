package com.vpbankhackathon.store_and_forward_service.pubsub.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpbankhackathon.store_and_forward_service.models.dtos.CustomerScreeningRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CustomerScreeningRequestProducer {
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private String topicName;

    public void sendMessage(CustomerScreeningRequest request) {
        try {

            System.out.println("Attempting to send message to topic: " + topicName);
            System.out.println("Message content: " + request.getCustomerId());

            CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topicName, request);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message for customer=[" + request.getCustomerId() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("Unable to send message for customer=[" +
                            request.getCustomerId() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in sendMessage: " + e.getMessage());
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}
