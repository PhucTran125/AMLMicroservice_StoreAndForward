package com.vpbankhackathon.store_and_forward_service.pubsub.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpbankhackathon.store_and_forward_service.models.dtos.Transaction;
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
public class TransactionConsumer {

    @Autowired
    private StoreAndForwardService storeAndForwardService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "aml-inbound-transaction")
    public void listenTransactionRequestMsg(
            @Payload Transaction transaction,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            System.out.println("Received transaction request: " + transaction.getCustomerId() +
                    " (ID: " + transaction.getId() + ") from topic: " + topic +
                    ", partition: " + partition + ", offset: " + offset);

            // Create AML request and store to database
            AMLRequest amlRequest = storeAndForwardService.createAMLRequestFromTransaction(transaction);

            System.out.println("Successfully processed transaction request. AML Request ID: " + amlRequest.getId());

            // Acknowledge message on successful processing
            acknowledgment.acknowledge();
            System.out.println("Message acknowledged successfully for transaction: " + transaction.getId());

        } catch (Exception e) {
            System.err.println("Error processing transaction request: " + e.getMessage());
            e.printStackTrace();

            // Store failed message for later review (optional)
//            storeFailedMessage(transactionDTO, "TRANSACTION", e.getMessage(), topic, partition, offset);

            // Acknowledge message even on error to prevent infinite retries
            acknowledgment.acknowledge();
            System.out.println("Message acknowledged after error for transaction: " + transaction.getId());
        }
    }
}
