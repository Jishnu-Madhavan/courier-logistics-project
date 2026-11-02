package com.courier.logistics.controller;

import com.courier.logistics.entity.ShipmentStatus;
import com.courier.logistics.entity.TrackingHistory;
import com.courier.logistics.repository.TrackingHistoryRepository;
import com.courier.logistics.service.LogisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/tracking-history")
public class TrackingHistoryController {
    private final TrackingHistoryRepository repository;
    private final LogisticsService service;

    public TrackingHistoryController(TrackingHistoryRepository repository, LogisticsService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<TrackingHistory> all() { return repository.findAllByOrderByTimestampAsc(); }

    @GetMapping("/{id}")
    public TrackingHistory get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracking history not found: " + id));
    }

    @GetMapping("/by-tracking-number")
    public List<TrackingHistory> byTrackingNumber(@RequestParam String trackingNumber) {
        List<TrackingHistory> result = repository.findByShipment_TrackingNumberOrderByTimestampAsc(trackingNumber.trim().toUpperCase());
        if (result.isEmpty()) {
            // Distinguish a shipment with no history from an unknown tracking number; new shipments always have history.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracking number not found or has no history");
        }
        return result;
    }

    @GetMapping("/by-status")
    public List<TrackingHistory> byStatus(@RequestParam ShipmentStatus status) { return repository.findByStatus(status); }

    @GetMapping("/shipment/{shipmentId}")
    public List<TrackingHistory> byShipment(@PathVariable Long shipmentId) {
        service.shipment(shipmentId);
        return repository.findByShipmentIdOrderByTimestampAsc(shipmentId);
    }
}
