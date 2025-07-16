package com.vpbankhackathon.store_and_forward_service.pubsub.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpbankhackathon.store_and_forward_service.models.dtos.AccountOpening;
import com.vpbankhackathon.store_and_forward_service.models.entities.AMLRequest;
import com.vpbankhackathon.store_and_forward_service.services.StoreAndForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class AccountOpeningConsumer {

    @Autowired
    private StoreAndForwardService storeAndForwardService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "aml-inbound-account-opening")
    public void listenAccountOpeningRequestMsg(
            @Payload AccountOpening accountOpening,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            System.out.println("Received account opening request: " + accountOpening.getId() +
                    " from topic: " + topic + ", partition: " + partition + ", offset: " + offset);

            // Create AML request and store to database
            AMLRequest amlRequest = storeAndForwardService.createAMLRequestFromAccountOpening(accountOpening);

            System.out.println("Successfully processed account opening request. AML Request ID: " + amlRequest.getId());

            // Acknowledge message on successful processing
            acknowledgment.acknowledge();
            System.out.println("Message acknowledged successfully for account opening: " + accountOpening.getId());

        } catch (Exception e) {
            System.err.println("Error processing account opening request: " + e.getMessage());
            e.printStackTrace();

            // Acknowledge message even on error to prevent infinite retries
            acknowledgment.acknowledge();
            System.out.println("Message acknowledged after error for account opening: " + accountOpening.getId());
        }
    }
}
