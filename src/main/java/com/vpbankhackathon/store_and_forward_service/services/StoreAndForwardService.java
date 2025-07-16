package com.vpbankhackathon.store_and_forward_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpbankhackathon.store_and_forward_service.models.dtos.*;
import com.vpbankhackathon.store_and_forward_service.models.entities.AMLRequest;
import com.vpbankhackathon.store_and_forward_service.pubsub.producers.CustomerScreeningRequestProducer;
import com.vpbankhackathon.store_and_forward_service.pubsub.producers.T24AMLResultProducer;
import com.vpbankhackathon.store_and_forward_service.pubsub.producers.TransactionMonitoringRequestProducer;
import com.vpbankhackathon.store_and_forward_service.repositories.AMLRequestRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Service
@EnableScheduling
public class StoreAndForwardService {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServers;

    @Autowired
    CustomerScreeningRequestProducer customerProducer;

    @Autowired
    TransactionMonitoringRequestProducer transactionProducer;

    @Autowired
    T24AMLResultProducer amlResultProducer;

    @Autowired
    AMLRequestRepository amlRequestRepository;

    @Autowired
    ObjectMapper objectMapper;

    // public void requestCustomerScreening(String customerId) {
    // // Create a CustomerScreeningRequest.java object
    // CustomerScreeningRequest request = new CustomerScreeningRequest();
    // request.setCustomerId(customerId);
    // request.setCustomerName("John Doe"); // Sample data - replace with real data
    // request.setIdentificationNumber("ID123456789");
    // request.setDob(LocalDate.of(1990, 1, 15));
    // request.setAddress("123 Main Street, City, Country");
    // request.setNationality("Vietnamese");
    // request.setTransactionId(UUID.randomUUID().toString());
    //
    // producer.sendMessage(request);
    // }

    // public void processTransaction(TransactionRequest request) {
    // try {
    // if (!validateAuth(request.getSignature())) {
    // throw new SecurityException("Invalid signature");
    // }
    //
    // Transaction transaction = mapToEntity(request);
    // transaction.setState("pending");
    // transactionRepository.save(transaction);
    //
    // String eventType = getEventType(request);
    // if ("ACCOUNT_OPENING".equals(eventType) || isCsActive() ||
    // request.getAmount() >= 400000000) {
    // sendToKafka(transaction);
    // transaction.setState("sent");
    // transactionRepository.save(transaction);
    // }
    // } catch (Exception e) {
    // throw new RuntimeException("Error processing transaction: " + e.getMessage(),
    // e);
    // }
    // }

    // private Transaction mapToEntity(TransactionRequest request) {
    // Transaction tx = new Transaction();
    // tx.setId(UUID.fromString(request.getTransactionId()));
    // tx.setTimestamp(LocalDateTime.parse(request.getTimestamp()));
    // try {
    // tx.setData(new ObjectMapper().writeValueAsString(request.getData()));
    // } catch (Exception e) {
    // tx.setData("{}");
    // }
    // return tx;
    // }

    // public void requestCustomerScreening(CustomerScreeningRequest request) {
    // // Generate transaction ID if not provided
    // if (request.getTransactionId() == null ||
    // request.getTransactionId().isEmpty()) {
    // request.setTransactionId(UUID.randomUUID().toString());
    // }
    // producer.sendMessage(request);
    // }

    public void requestCustomerScreening(String customerId) {
        // Create a CustomerScreeningRequest object
        CustomerScreeningRequest request = new CustomerScreeningRequest();
        request.setCustomerId(customerId);
        request.setCustomerName("John Doe"); // Sample data - replace with real data
        request.setIdentificationNumber("ID123456789");
        request.setDob(LocalDate.of(1990, 1, 15));
        request.setAddress("123 Main Street, City, Country");
        request.setNationality("Vietnamese");
        request.getRequestId();

        customerProducer.sendMessage(request);
    }

    public void requestCustomerScreening(CustomerScreeningRequest request) {
        // Generate transaction ID if not provided
        if (request.getRequestId() == null || request.getRequestId().isEmpty()) {
            request.getRequestId();
        }
        customerProducer.sendMessage(request);
    }

    public AMLRequest createAMLRequest(Object requestData, String requestType) {
        try {
            AMLRequest amlRequest = new AMLRequest();
            amlRequest.setStatus(AMLRequest.RequestStatus.PENDING);

            // Create a wrapper object that includes the request type and data
            Map<String, Object> dataWrapper = new HashMap<>();
            dataWrapper.put("requestType", requestType);
            dataWrapper.put("requestData", requestData);
            dataWrapper.put("createdAt", LocalDateTime.now());

            // Convert to JSON string for storage
            String jsonData = objectMapper.writeValueAsString(dataWrapper);
            amlRequest.setData(jsonData);

            // Save to database
            AMLRequest savedRequest = amlRequestRepository.save(amlRequest);

            System.out.println("Created AML Request with ID: " + savedRequest.getId() + " for type: " + requestType);
            return savedRequest;

        } catch (Exception e) {
            System.err.println("Error creating AML request: " + e.getMessage());
            throw new RuntimeException("Failed to create AML request", e);
        }
    }

    public AMLRequest createAMLRequestFromAccountOpening(AccountOpening accountOpening) {
        // Account opening typically requires customer screening
        CustomerScreeningRequest screeningRequest = convertToCustomerScreeningRequest(accountOpening);
        return createAMLRequest(screeningRequest, "CUSTOMER_SCREENING");
    }

    public AMLRequest createAMLRequestFromTransaction(Transaction transaction) {
        // High-value transactions or flagged transactions go to transaction monitoring
        TransactionMonitoringRequest monitoringRequest = convertToTransactionMonitoringRequest(transaction);
        return createAMLRequest(monitoringRequest, "TRANSACTION_MONITORING");
    }

    private CustomerScreeningRequest convertToCustomerScreeningRequest(AccountOpening accountOpening) {
        CustomerScreeningRequest request = new CustomerScreeningRequest();
        request.setCustomerId(
                accountOpening.getId() != null ? accountOpening.getId().toString() : UUID.randomUUID().toString());
        request.setCustomerName(accountOpening.getCustomerName());
        request.setIdentificationNumber(accountOpening.getCustomerIdentificationNumber());
        request.setAddress(accountOpening.getResidentialAddress());
        request.setNationality(accountOpening.getNationality());
        request.setRequestId(UUID.randomUUID().toString());
        return request;
    }

    private TransactionMonitoringRequest convertToTransactionMonitoringRequest(Transaction transaction) {
        TransactionMonitoringRequest request = new TransactionMonitoringRequest();
        request.setTransactionId(transaction.getId());
        request.setCustomerId(transaction.getCustomerId());
        request.setCustomerName(transaction.getCustomerName());
        request.setCustomerIdentificationNumber(transaction.getCustomerIdentificationNumber());
        request.setAmount(transaction.getAmount());
        request.setCurrency(transaction.getCurrency());
        request.setSourceAccountNumber(transaction.getSourceAccountNumber());
        request.setDestinationAccountNumber(transaction.getDestinationAccountNumber());
        request.setTimestamp(Instant.now().toEpochMilli());
        return request;
    }

    private String determineRiskLevel(Transaction transaction) {
        if (transaction.getAmount() >= 1000000000) { // 1B VND
            return "HIGH";
        } else if (transaction.getAmount() >= 100000000) { // 100M VND
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void sendPendingRequests() {
        if (isCsActive()) {
            List<AMLRequest> pending = amlRequestRepository.findByStatusOrderByTimestampAsc(
                AMLRequest.RequestStatus.PENDING,
                PageRequest.of(0, 100));
            for (AMLRequest amlRequest : pending) {
                try {
                    // Classify and send using appropriate producer
                    sendAMLRequestToAppropriateProducer(amlRequest);
                    amlRequest.setStatus(AMLRequest.RequestStatus.SENT);
                    amlRequestRepository.save(amlRequest);
                } catch (Exception e) {
                    System.err.println("Failed to send AML request " + amlRequest.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    private void sendAMLRequestToAppropriateProducer(AMLRequest amlRequest) throws Exception {
        // Parse the stored JSON data to determine request type
        Map<String, Object> dataWrapper = objectMapper.readValue(amlRequest.getData(), Map.class);
        String requestType = (String) dataWrapper.get("requestType");
        Object requestData = dataWrapper.get("requestData");

        switch (requestType) {
            case "CUSTOMER_SCREENING":
                // Convert to CustomerScreeningRequest and send via customer producer
                CustomerScreeningRequest customerRequest = objectMapper.convertValue(requestData,
                        CustomerScreeningRequest.class);
                customerProducer.sendMessage(customerRequest);
                System.out.println("Sent customer screening request for AML ID: " + amlRequest.getId());
                break;

            case "TRANSACTION_MONITORING":
                // Convert to TransactionMonitoringRequest and send via transaction producer
                TransactionMonitoringRequest transactionRequest = objectMapper.convertValue(requestData,
                        TransactionMonitoringRequest.class);
                transactionProducer.sendMessage(transactionRequest);
                System.out.println("Sent transaction monitoring request for AML ID: " + amlRequest.getId());
                break;

            default:
                System.err.println("Unknown request type: " + requestType + " for AML ID: " + amlRequest.getId());
                break;
        }
    }

    private boolean isCsActive() {
        // Use Kafka AdminClient to check consumer group lag
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaServers);
        try (AdminClient adminClient = AdminClient.create(props)) {
            // Simplified placeholder; implement lag check
            return true;
        }
    }

    public void forwardTransactionMonitoringResult(TransactionMonitoringResult result) {
        T24AMLResult amlResult = new T24AMLResult();
        amlResult.setType("TRANSACTION_MONITORING_RESULT");
        amlResult.setId(result.getTransactionId());
        amlResult.setStatus(result.getStatus());
        amlResult.setReason(result.getReason());
        amlResultProducer.sendMessage(amlResult);
    }

    public void forwardCustomerScreeningResult(CustomerScreeningResult result) {
        T24AMLResult amlResult = new T24AMLResult();
        amlResult.setType("CUSTOMER_SCREENING_RESULT");
        amlResult.setId(result.getCustomerId());
        amlResult.setStatus(result.getStatus());
        amlResult.setReason(result.getReason());
        amlResultProducer.sendMessage(amlResult);
    }
}
