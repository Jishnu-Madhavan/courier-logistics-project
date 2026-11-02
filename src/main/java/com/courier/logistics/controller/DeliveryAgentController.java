package com.courier.logistics.controller;

import com.courier.logistics.entity.*;
import com.courier.logistics.repository.DeliveryAgentRepository;
import com.courier.logistics.service.LogisticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/delivery-agents")
public class DeliveryAgentController {
    private final DeliveryAgentRepository repository;
    private final LogisticsService service;

    public DeliveryAgentController(DeliveryAgentRepository repository, LogisticsService service) {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryAgent create(@Valid @RequestBody DeliveryAgent agent) {
        agent.setId(null);
        agent.setContact(agent.getContact().trim());
        agent.setVehicleNo(agent.getVehicleNo().trim().toUpperCase(java.util.Locale.ROOT));
        service.validateUniqueContact(agent.getContact(), "agent", null);
        return repository.save(agent);
    }

    @GetMapping
    public List<DeliveryAgent> all() { return repository.findAll(); }

    @GetMapping("/available")
    public List<DeliveryAgent> available() { return repository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE); }

    @GetMapping("/by-vehicle")
    public DeliveryAgent byVehicle(@RequestParam String vehicleNo) {
        return repository.findByVehicleNoIgnoreCase(vehicleNo.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle number not found"));
    }

    @GetMapping("/by-contact")
    public DeliveryAgent byContact(@RequestParam String contact) {
        return repository.findByContact(contact.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery agent contact not found"));
    }

    @GetMapping("/by-min-rating")
    public List<DeliveryAgent> byMinimumRating(@RequestParam Double rating) {
        if (rating == null || rating < 0 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be between 0 and 5");
        }
        return repository.findByRatingGreaterThanEqual(rating);
    }

    @GetMapping("/sort-by-rating")
    public List<DeliveryAgent> sortByRating(@RequestParam(defaultValue = "desc") String direction) {
        if ("asc".equalsIgnoreCase(direction)) return repository.findAllByOrderByRatingAsc();
        if ("desc".equalsIgnoreCase(direction)) return repository.findAllByOrderByRatingDesc();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be asc or desc");
    }

    @GetMapping("/{id}")
    public DeliveryAgent get(@PathVariable Long id) { return service.agent(id); }

    @PutMapping("/{id}")
    public DeliveryAgent update(@PathVariable Long id, @Valid @RequestBody DeliveryAgent request) {
        DeliveryAgent agent = service.agent(id);
        service.validateUniqueContact(request.getContact(), "agent", id);
        agent.setName(request.getName());
        agent.setContact(request.getContact().trim());
        agent.setVehicleNo(request.getVehicleNo().trim().toUpperCase(java.util.Locale.ROOT));
        agent.setRating(request.getRating());
        return repository.save(agent);
    }

    @PatchMapping("/{id}/availability")
    public DeliveryAgent availability(@PathVariable Long id, @RequestParam AvailabilityStatus status) {
        return service.updateAgentAvailability(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteAgent(id); }
}
