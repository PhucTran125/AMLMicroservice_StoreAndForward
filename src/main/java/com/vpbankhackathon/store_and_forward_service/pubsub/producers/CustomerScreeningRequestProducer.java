package com.vpbankhackathon.store_and_forward_service.pubsub.producers;

import com.vpbankhackathon.store_and_forward_service.models.dtos.CustomerScreeningRequest;
import com.vpbankhackathon.store_and_forward_service.models.entities.AMLRequest;
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

    @Value("${spring.kafka.producer.topic.customer-screening-requests}")
    private String topicName;

    public void sendMessage(AMLRequest request) {
        try {

            System.out.println("Attempting to send message to topic: " + topicName);
            System.out.println("Message content: " + request.getId());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, request);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message for customer=[" + request.getId() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("Unable to send message for customer=[" +
                            request.getId() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in sendMessage: " + e.getMessage());
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }

    public void sendMessage(CustomerScreeningRequest request) {
        try {
            System.out.println("Attempting to send customer screening request to topic: " + topicName);
            System.out.println("Customer ID: " + request.getCustomerId());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName,
                String.valueOf(request.getCustomerId()), request);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent customer screening request=[" + request.getCustomerId() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("Unable to send customer screening request=[" +
                            request.getCustomerId() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in sendMessage (Customer Screening): " + e.getMessage());
            throw new RuntimeException("Failed to send customer screening request to Kafka", e);
        }
    }
}
