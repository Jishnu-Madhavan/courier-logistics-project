package com.courier.logistics.controller;

import com.courier.logistics.entity.Customer;
import com.courier.logistics.entity.PaymentMethod;
import com.courier.logistics.entity.Shipment;
import com.courier.logistics.repository.CustomerRepository;
import com.courier.logistics.repository.ShipmentRepository;
import com.courier.logistics.service.LogisticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Locale;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository repository;
    private final LogisticsService service;
    private final ShipmentRepository shipments;

    public CustomerController(CustomerRepository repository, LogisticsService service, ShipmentRepository shipments) {
        this.repository = repository;
        this.service = service;
        this.shipments = shipments;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody Customer customer) {
        customer.setId(null);
        customer.setEmail(customer.getEmail().trim().toLowerCase(Locale.ROOT));
        customer.setContact(customer.getContact().trim());
        service.validateUniqueContact(customer.getContact(), "customer", null);
        return repository.save(customer);
    }

    @GetMapping
    public List<Customer> all() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) { return service.customer(id); }

    @GetMapping("/by-email")
    public Customer byEmail(@RequestParam String email) {
        return repository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer email not found"));
    }

    @GetMapping("/by-contact")
    public Customer byContact(@RequestParam String contact) {
        return repository.findByContact(contact.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer contact not found"));
    }

    @GetMapping("/by-payment-method")
    public List<Customer> byPaymentMethod(
            @RequestParam PaymentMethod method,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        List<Customer> matches = shipments.findByPayment_PaymentMethod(method).stream()
                .map(Shipment::getCustomer)
                .collect(Collectors.toMap(Customer::getId, customer -> customer, (first, ignored) -> first,
                        LinkedHashMap::new))
                .values().stream().collect(Collectors.toList());
        Comparator<Customer> comparator;
        if ("name".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER);
        } else if ("email".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Customer::getEmail, String.CASE_INSENSITIVE_ORDER);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortBy must be name or email");
        }
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        else if (!"asc".equalsIgnoreCase(direction)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be asc or desc");
        }
        matches.sort(comparator);
        return matches;
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer request) {
        Customer customer = service.customer(id);
        service.validateUniqueContact(request.getContact(), "customer", id);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        customer.setContact(request.getContact().trim());
        customer.setAddress(request.getAddress());
        return repository.save(customer);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteCustomer(id); }
}
