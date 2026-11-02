package com.courier.logistics.repository;

import com.courier.logistics.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByShipment_Customer_Id(Long customerId);
}
