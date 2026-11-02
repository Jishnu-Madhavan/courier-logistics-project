package com.courier.logistics.repository;

import com.courier.logistics.entity.AvailabilityStatus;
import com.courier.logistics.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    List<DeliveryAgent> findByAvailabilityStatus(AvailabilityStatus status);
    Optional<DeliveryAgent> findByVehicleNoIgnoreCase(String vehicleNo);
    Optional<DeliveryAgent> findByContact(String contact);
    List<DeliveryAgent> findByRatingGreaterThanEqual(Double rating);
    List<DeliveryAgent> findAllByOrderByRatingAsc();
    List<DeliveryAgent> findAllByOrderByRatingDesc();
}
