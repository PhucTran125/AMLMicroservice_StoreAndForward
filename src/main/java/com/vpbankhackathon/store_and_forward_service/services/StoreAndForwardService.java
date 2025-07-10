package com.vpbankhackathon.store_and_forward_service.services;

import com.vpbankhackathon.store_and_forward_service.models.dtos.CustomerScreeningRequest;
import com.vpbankhackathon.store_and_forward_service.pubsub.producers.CustomerScreeningRequestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class StoreAndForwardService {
    @Autowired
    CustomerScreeningRequestProducer producer;

    public void requestCustomerScreening(String customerId) {
        // Create a CustomerScreeningRequest object
        CustomerScreeningRequest request = new CustomerScreeningRequest();
        request.setCustomerId(customerId);
        request.setCustomerName("John Doe"); // Sample data - replace with real data
        request.setIdentificationNumber("ID123456789");
        request.setDob(LocalDate.of(1990, 1, 15));
        request.setAddress("123 Main Street, City, Country");
        request.setNationality("Vietnamese");
        request.setTransactionId(UUID.randomUUID().toString());

        producer.sendMessage(request);
    }

    public void requestCustomerScreening(CustomerScreeningRequest request) {
        // Generate transaction ID if not provided
        if (request.getTransactionId() == null || request.getTransactionId().isEmpty()) {
            request.setTransactionId(UUID.randomUUID().toString());
        }
        producer.sendMessage(request);
    }
}
