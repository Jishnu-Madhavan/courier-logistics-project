package com.courier.logistics.repository;

import com.courier.logistics.entity.Shipment;
import com.courier.logistics.entity.ShipmentStatus;
import com.courier.logistics.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findByCustomerId(Long customerId);
    List<Shipment> findByWarehouseId(Long warehouseId);
    List<Shipment> findByDeliveryAgentId(Long deliveryAgentId);
    List<Shipment> findByDeliveryDate(LocalDate deliveryDate);
    List<Shipment> findBySourceContainingIgnoreCase(String source);
    List<Shipment> findByDestinationContainingIgnoreCase(String destination);
    List<Shipment> findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCase(String source, String destination);
    List<Shipment> findByPackageEntity_PackageTypeIgnoreCase(String packageType);
    List<Shipment> findByStatus(ShipmentStatus status);
    List<Shipment> findByPayment_PaymentMethod(PaymentMethod paymentMethod);
    List<Shipment> findAllByOrderByWeightAsc();
    List<Shipment> findAllByOrderByWeightDesc();
    boolean existsByWarehouseId(Long warehouseId);
    boolean existsByDeliveryAgentId(Long agentId);
}
