package com.vpbankhackathon.store_and_forward_service.pubsub.consumers;

import com.vpbankhackathon.store_and_forward_service.models.dtos.AccountOpening;
import com.vpbankhackathon.store_and_forward_service.models.dtos.VerifyCustomerRequestDTO;
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
public class VerifyCustomerConsumer {

    @Autowired
    private StoreAndForwardService storeAndForwardService;

    @KafkaListener(topics = "aml-inbound-verify-customer")
    public void listenVerifyCustomerRequestMsg(
        @Payload VerifyCustomerRequestDTO requestDTO,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        try {
            System.out.println("Received verify customer request: " + requestDTO.getCustomerId() +
                " from topic: aml-inbound-verify-customer" + ", partition: " + partition + ", offset: " + offset);

            // Create AML request and store to database
            storeAndForwardService.createAMLRequestFromVerifyCustomerReq(requestDTO);

            // Acknowledge message on successful processing
            acknowledgment.acknowledge();
            System.out.println("Message acknowledged successfully for verify customer request: " + requestDTO.getCustomerId());
        } catch (Exception e) {
            System.err.println("Error processing verify customer request: " + e.getMessage());
            e.printStackTrace();

            // Acknowledge message even on error to prevent infinite retries
            acknowledgment.acknowledge();
            System.out.println("Message acknowledged after error for account opening: " + requestDTO.getCustomerId());
        }
    }

}
