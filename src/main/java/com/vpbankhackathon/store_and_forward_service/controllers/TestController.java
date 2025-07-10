package com.vpbankhackathon.store_and_forward_service.controllers;

import com.vpbankhackathon.store_and_forward_service.models.dtos.CustomerScreeningRequest;
import com.vpbankhackathon.store_and_forward_service.services.StoreAndForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private StoreAndForwardService service;

    @GetMapping("/send-message")
    public ResponseEntity<String> sendMessage() {
        try {
            System.out.println("Sending message to customer screening service...");
            service.requestCustomerScreening("12345");
            return ResponseEntity.ok("Message sent successfully to customer screening service");
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to send message: " + e.getMessage());
        }
    }

    @PostMapping("/send-customer-request")
    public ResponseEntity<String> sendCustomerRequest(@RequestBody CustomerScreeningRequest request) {
        try {
            System.out.println("Sending customer screening request: " + request.getCustomerId());
            service.requestCustomerScreening(request);
            return ResponseEntity.ok("Customer screening request sent successfully");
        } catch (Exception e) {
            System.err.println("Error sending customer request: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to send customer request: " + e.getMessage());
        }
    }
}
