package com.vpbankhackathon.store_and_forward_service.pubsub.producers;

import com.vpbankhackathon.store_and_forward_service.models.dtos.TransactionMonitoringRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class TransactionMonitoringRequestProducer {
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.producer.topic.transaction-monitoring-topic}")
    private String topicName;

    public void sendMessage(TransactionMonitoringRequest request) {
        try {
            System.out.println("Attempting to send transaction monitoring request to topic: " + topicName);
            System.out.println("Transaction ID: " + request.getTransactionId());
            System.out.println("Customer ID: " + request.getCustomerId());
            System.out.println("Amount: " + request.getAmount() + " " + request.getCurrency());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName,
                    request.getTransactionId().toString(), request);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent transaction monitoring request=[" + request.getTransactionId() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("Unable to send transaction monitoring request=[" +
                            request.getTransactionId() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in sendMessage (Transaction Monitoring): " + e.getMessage());
            throw new RuntimeException("Failed to send transaction monitoring request to Kafka", e);
        }
    }

    // Keep the generic method for backward compatibility
    public void sendMessage(Object request) {
        try {
            System.out.println("Attempting to send generic request to topic: " + topicName);
            System.out.println("Request content: " + request.toString());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, request);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent generic request with offset: " + result.getRecordMetadata().offset());
                } else {
                    System.err.println("Failed to send generic request: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in sendMessage (Generic): " + e.getMessage());
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}
