package com.courier.logistics.repository;

import com.courier.logistics.entity.ShipmentStatus;
import com.courier.logistics.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory, Long> {
    List<TrackingHistory> findByShipmentIdOrderByTimestampAsc(Long shipmentId);
    List<TrackingHistory> findByShipment_TrackingNumberOrderByTimestampAsc(String trackingNumber);
    List<TrackingHistory> findByStatus(ShipmentStatus status);
    List<TrackingHistory> findAllByOrderByTimestampAsc();
}
