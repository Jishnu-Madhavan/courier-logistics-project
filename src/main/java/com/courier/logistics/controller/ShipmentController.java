package com.courier.logistics.controller;

import com.courier.logistics.dto.AssignmentRequest;
import com.courier.logistics.dto.DestinationUpdateRequest;
import com.courier.logistics.dto.ShipmentRequest;
import com.courier.logistics.dto.StatusUpdateRequest;
import com.courier.logistics.dto.WarehouseAssignmentRequest;
import com.courier.logistics.entity.Shipment;
import com.courier.logistics.entity.ShipmentStatus;
import com.courier.logistics.entity.TrackingHistory;
import com.courier.logistics.repository.ShipmentRepository;
import com.courier.logistics.repository.TrackingHistoryRepository;
import com.courier.logistics.service.LogisticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
    private final ShipmentRepository repository;
    private final LogisticsService service;
    private final TrackingHistoryRepository histories;

    public ShipmentController(ShipmentRepository repository, LogisticsService service, TrackingHistoryRepository histories) {
        this.repository = repository;
        this.service = service;
        this.histories = histories;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment create(@Valid @RequestBody ShipmentRequest request) { return service.createShipment(request); }

    @GetMapping
    public List<Shipment> all() { return service.allShipments(); }

    @GetMapping("/{id}")
    public Shipment get(@PathVariable Long id) { return service.shipment(id); }

    @GetMapping("/tracking/{trackingNumber}")
    public Shipment track(@PathVariable String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber.trim().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracking number not found"));
    }

    @GetMapping("/{id}/history")
    public List<TrackingHistory> history(@PathVariable Long id) {
        service.shipment(id);
        return histories.findByShipmentIdOrderByTimestampAsc(id);
    }

    @PatchMapping("/{id}/status")
    public Shipment status(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(id, request);
    }

    @PatchMapping("/{id}/assign-agent")
    public Shipment assignAgent(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        return service.assignAgent(id, request.getDeliveryAgentId());
    }

    @PatchMapping("/{id}/assign-warehouse")
    public Shipment assignWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseAssignmentRequest request) {
        return service.assignWarehouse(id, request.getWarehouseId());
    }

    @PatchMapping("/{id}/destination")
    public Shipment updateDestination(@PathVariable Long id, @Valid @RequestBody DestinationUpdateRequest request) {
        return service.updateDestination(id, request);
    }

    @GetMapping("/by-customer/{customerId}")
    public List<Shipment> byCustomer(@PathVariable Long customerId) { return repository.findByCustomerId(customerId); }

    @GetMapping("/by-warehouse/{warehouseId}")
    public List<Shipment> byWarehouse(@PathVariable Long warehouseId) { return repository.findByWarehouseId(warehouseId); }

    @GetMapping("/by-agent/{agentId}")
    public List<Shipment> byAgent(@PathVariable Long agentId) { return repository.findByDeliveryAgentId(agentId); }

    @GetMapping("/by-delivery-date")
    public List<Shipment> byDeliveryDate(@RequestParam LocalDate date) { return repository.findByDeliveryDate(date); }

    @GetMapping("/by-source")
    public List<Shipment> bySource(@RequestParam String source) { return repository.findBySourceContainingIgnoreCase(source.trim()); }

    @GetMapping("/by-destination")
    public List<Shipment> byDestination(@RequestParam String destination) { return repository.findByDestinationContainingIgnoreCase(destination.trim()); }

    @GetMapping("/by-route")
    public List<Shipment> byRoute(@RequestParam String source, @RequestParam String destination) {
        return repository.findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCase(source.trim(), destination.trim());
    }

    @GetMapping("/by-package-type")
    public List<Shipment> byPackageType(@RequestParam String packageType) {
        return repository.findByPackageEntity_PackageTypeIgnoreCase(packageType.trim());
    }

    @GetMapping("/by-status")
    public List<Shipment> byStatus(@RequestParam ShipmentStatus status) { return repository.findByStatus(status); }

    @GetMapping("/sort-by-weight")
    public List<Shipment> sortWeight(@RequestParam(defaultValue = "asc") String direction) {
        if ("asc".equalsIgnoreCase(direction)) return repository.findAllByOrderByWeightAsc();
        if ("desc".equalsIgnoreCase(direction)) return repository.findAllByOrderByWeightDesc();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be asc or desc");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteShipment(id); }
}
