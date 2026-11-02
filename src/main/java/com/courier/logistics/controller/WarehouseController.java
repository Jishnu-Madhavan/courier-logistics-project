package com.courier.logistics.controller;

import com.courier.logistics.entity.Warehouse;
import com.courier.logistics.repository.WarehouseRepository;
import com.courier.logistics.service.LogisticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseRepository repository;
    private final LogisticsService service;

    public WarehouseController(WarehouseRepository repository, LogisticsService service) {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Warehouse create(@Valid @RequestBody Warehouse warehouse) {
        warehouse.setId(null);
        warehouse.setContact(warehouse.getContact().trim());
        service.validateUniqueContact(warehouse.getContact(), "warehouse", null);
        return repository.save(warehouse);
    }

    @GetMapping
    public List<Warehouse> all() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Warehouse get(@PathVariable Long id) { return service.warehouse(id); }

    @GetMapping("/by-location")
    public List<Warehouse> byLocation(@RequestParam String location) {
        return repository.findByLocationContainingIgnoreCase(location.trim());
    }

    @GetMapping("/by-capacity")
    public List<Warehouse> byCapacity(@RequestParam Integer capacity) {
        return repository.findByCapacity(capacity);
    }

    @GetMapping("/sort-by-capacity")
    public List<Warehouse> sortByCapacity(@RequestParam(defaultValue = "asc") String direction) {
        if ("asc".equalsIgnoreCase(direction)) return repository.findAllByOrderByCapacityAsc();
        if ("desc".equalsIgnoreCase(direction)) return repository.findAllByOrderByCapacityDesc();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be asc or desc");
    }

    @PutMapping("/{id}")
    public Warehouse update(@PathVariable Long id, @Valid @RequestBody Warehouse request) {
        Warehouse warehouse = service.warehouse(id);
        service.validateUniqueContact(request.getContact(), "warehouse", id);
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setContact(request.getContact().trim());
        return repository.save(warehouse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteWarehouse(id); }
}
