package com.vpbankhackathon.store_and_forward_service.pubsub.producers;

import com.vpbankhackathon.store_and_forward_service.models.dtos.T24AMLResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class T24AMLResultProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.producer.topic.t24-aml-result}")
    private String topicName;

    public void sendMessage(T24AMLResult amlResult) {
        try {
            System.out.println("Attempting to send message to topic: " + topicName);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, amlResult);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("Unable to send message due to : " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in sendMessage: " + e.getMessage());
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }

}
