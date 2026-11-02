package com.courier.logistics.controller;

import com.courier.logistics.entity.Payment;
import com.courier.logistics.entity.PaymentStatus;
import com.courier.logistics.repository.PaymentRepository;
import com.courier.logistics.service.LogisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentRepository repository;
    private final LogisticsService service;

    public PaymentController(PaymentRepository repository, LogisticsService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<Payment> all() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Payment get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found: " + id));
    }

    @GetMapping("/by-customer/{customerId}")
    public List<Payment> byCustomer(@PathVariable Long customerId) {
        return repository.findByShipment_Customer_Id(customerId);
    }

    @PatchMapping("/{id}/status")
    public Payment status(@PathVariable Long id, @RequestParam PaymentStatus status) {
        return service.updatePaymentStatus(id, status);
    }
}
