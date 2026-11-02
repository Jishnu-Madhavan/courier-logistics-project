package com.courier.logistics.repository;

import com.courier.logistics.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByContact(String contact);
    Optional<Customer> findByEmailIgnoreCase(String email);
    Optional<Customer> findByContact(String contact);
}
