package com.courier.logistics.controller;

import com.courier.logistics.entity.PackageEntity;
import com.courier.logistics.repository.PackageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {
    private final PackageRepository repository;
    public PackageController(PackageRepository repository) { this.repository = repository; }

    @GetMapping
    public List<PackageEntity> all() { return repository.findAll(); }

    @GetMapping("/{id}")
    public PackageEntity get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Package entity not found: " + id));
    }

    @GetMapping("/by-type")
    public List<PackageEntity> byType(@RequestParam String packageType) {
        return repository.findByPackageTypeIgnoreCase(packageType.trim());
    }
}
