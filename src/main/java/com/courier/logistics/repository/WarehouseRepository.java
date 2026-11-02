package com.courier.logistics.repository;

import com.courier.logistics.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByLocationContainingIgnoreCase(String location);
    java.util.Optional<Warehouse> findByContact(String contact);
    List<Warehouse> findByCapacity(Integer capacity);
    List<Warehouse> findAllByOrderByCapacityAsc();
    List<Warehouse> findAllByOrderByCapacityDesc();
}
