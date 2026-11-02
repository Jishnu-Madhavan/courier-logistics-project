package com.courier.logistics.service;

import com.courier.logistics.dto.DestinationUpdateRequest;
import com.courier.logistics.dto.ShipmentRequest;
import com.courier.logistics.dto.StatusUpdateRequest;
import com.courier.logistics.entity.*;
import com.courier.logistics.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LogisticsService {
    private final CustomerRepository customers;
    private final DeliveryAgentRepository agents;
    private final WarehouseRepository warehouses;
    private final ShipmentRepository shipments;
    private final PaymentRepository payments;
    private final PackageRepository packages;

    public LogisticsService(CustomerRepository customers, DeliveryAgentRepository agents,
                            WarehouseRepository warehouses, ShipmentRepository shipments,
                            PaymentRepository payments, PackageRepository packages) {
        this.customers = customers;
        this.agents = agents;
        this.warehouses = warehouses;
        this.shipments = shipments;
        this.payments = payments;
        this.packages = packages;
    }

    public Customer customer(Long id) {
        return customers.findById(id).orElseThrow(() -> notFound("Customer", id));
    }

    public DeliveryAgent agent(Long id) {
        return agents.findById(id).orElseThrow(() -> notFound("Delivery agent", id));
    }

    public Warehouse warehouse(Long id) {
        return warehouses.findById(id).orElseThrow(() -> notFound("Warehouse", id));
    }

    public Shipment shipment(Long id) {
        return shipments.findById(id).orElseThrow(() -> notFound("Shipment", id));
    }

    private ResponseStatusException notFound(String type, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " not found: " + id);
    }


    /** Enforces a single contact number across customer, agent, and warehouse records. */
    public void validateUniqueContact(String contact, String entityType, Long existingId) {
        String normalized = contact == null ? "" : contact.trim();
        customers.findByContact(normalized).ifPresent(found -> {
            if (!("customer".equals(entityType) && found.getId().equals(existingId))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Contact number is already in use");
            }
        });
        agents.findByContact(normalized).ifPresent(found -> {
            if (!("agent".equals(entityType) && found.getId().equals(existingId))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Contact number is already in use");
            }
        });
        warehouses.findByContact(normalized).ifPresent(found -> {
            if (!("warehouse".equals(entityType) && found.getId().equals(existingId))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Contact number is already in use");
            }
        });
    }

    @Transactional
    public DeliveryAgent updateAgentAvailability(Long id, AvailabilityStatus requestedStatus) {
        DeliveryAgent agent = agent(id);
        boolean hasActiveShipment = agent.getShipments().stream().anyMatch(s -> !isTerminal(s.getStatus()));
        if (requestedStatus == AvailabilityStatus.AVAILABLE && hasActiveShipment) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agent has active shipments");
        }
        if (requestedStatus != AvailabilityStatus.AVAILABLE && requestedStatus != AvailabilityStatus.ASSIGNED
                && hasActiveShipment) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agent has active shipments and cannot be marked unavailable");
        }
        agent.setAvailabilityStatus(requestedStatus);
        return agents.save(agent);
    }

    @Transactional
    public Shipment createShipment(ShipmentRequest request) {
        Customer customer = customer(request.getCustomerId());
        Warehouse warehouse = warehouse(request.getWarehouseId());
        DeliveryAgent agent = agent(request.getDeliveryAgentId());
        if (agent.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Delivery agent is not available");
        }

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setSource(request.getSource().trim());
        shipment.setDestination(request.getDestination().trim());
        shipment.setWeight(request.getWeight());
        shipment.setDeliveryDate(request.getDeliveryDate());
        shipment.setShipmentDateTime(LocalDateTime.now());
        shipment.setStatus(ShipmentStatus.ORDER_PLACED);
        shipment.setCustomer(customer);
        shipment.setWarehouse(warehouse);
        shipment.setDeliveryAgent(agent);
        agent.setAvailabilityStatus(AvailabilityStatus.ASSIGNED);
        agents.save(agent);

        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageType(request.getPackageType() == null || request.getPackageType().isBlank()
                ? "STANDARD" : request.getPackageType().trim());
        packageEntity.setFragile(Boolean.TRUE.equals(request.getFragile()));
        packageEntity.setLength(request.getLength());
        packageEntity.setBreadth(request.getBreadth());
        packageEntity.setHeight(request.getHeight());
        shipment.setPackageEntity(packageEntity);

        Payment payment = new Payment();
        payment.setAmount(request.getPaymentAmount() == null
                ? calculateCharge(request.getWeight(), packageEntity) : request.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod() == null ? PaymentMethod.COD : request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        shipment.setPayment(payment);

        addHistory(shipment,
                request.getInitialLocation() == null || request.getInitialLocation().isBlank()
                        ? shipment.getSource() : request.getInitialLocation().trim(),
                ShipmentStatus.ORDER_PLACED);
        return shipments.save(shipment);
    }

    private String generateTrackingNumber() {
        String number;
        do {
            number = "CR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (shipments.findByTrackingNumber(number).isPresent());
        return number;
    }

    /** Default pricing rule: ₹80 base + ₹25 per kg above 1 kg + ₹50 for fragile handling. */
    private double calculateCharge(Double weight, PackageEntity packageEntity) {
        double amount = 80.0 + Math.max(0.0, weight - 1.0) * 25.0;
        if (Boolean.TRUE.equals(packageEntity.getFragile())) amount += 50.0;
        return Math.round(amount * 100.0) / 100.0;
    }

    private void addHistory(Shipment shipment, String location, ShipmentStatus status) {
        TrackingHistory history = new TrackingHistory();
        history.setLocation(location);
        history.setTimestamp(LocalDateTime.now());
        history.setStatus(status);
        history.setShipment(shipment);
        shipment.getTrackingHistory().add(history);
    }

    @Transactional
    public Shipment updateStatus(Long id, StatusUpdateRequest request) {
        Shipment shipment = shipment(id);
        shipment.setStatus(request.getStatus());
        String location = request.getLocation() == null || request.getLocation().isBlank()
                ? shipment.getDestination() : request.getLocation().trim();
        addHistory(shipment, location, request.getStatus());
        if (isTerminal(request.getStatus()) && shipment.getDeliveryAgent() != null) {
            shipment.getDeliveryAgent().setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            agents.save(shipment.getDeliveryAgent());
        }
        return shipments.save(shipment);
    }

    @Transactional
    public Shipment assignAgent(Long shipmentId, Long agentId) {
        Shipment shipment = shipment(shipmentId);
        DeliveryAgent next = agent(agentId);
        boolean sameAgent = shipment.getDeliveryAgent() != null
                && shipment.getDeliveryAgent().getId().equals(agentId);
        if (!sameAgent && next.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Delivery agent is not available");
        }
        if (sameAgent) return shipment;

        DeliveryAgent previous = shipment.getDeliveryAgent();
        if (previous != null) {
            previous.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            agents.save(previous);
        }
        shipment.setDeliveryAgent(next);
        next.setAvailabilityStatus(AvailabilityStatus.ASSIGNED);
        agents.save(next);
        addHistory(shipment, "Delivery agent assigned: " + next.getName(), shipment.getStatus());
        return shipments.save(shipment);
    }

    @Transactional
    public Shipment assignWarehouse(Long shipmentId, Long warehouseId) {
        Shipment shipment = shipment(shipmentId);
        Warehouse target = warehouse(warehouseId);
        shipment.setWarehouse(target);
        addHistory(shipment, "Warehouse assigned: " + target.getName(), shipment.getStatus());
        return shipments.save(shipment);
    }

    @Transactional
    public Shipment updateDestination(Long shipmentId, DestinationUpdateRequest request) {
        Shipment shipment = shipment(shipmentId);
        shipment.setDestination(request.getDestination().trim());
        addHistory(shipment, "Destination updated: " + shipment.getDestination(), shipment.getStatus());
        return shipments.save(shipment);
    }

    @Transactional
    public void deleteShipment(Long id) {
        deleteShipmentRecord(shipment(id));
    }

    private void deleteShipmentRecord(Shipment shipment) {
        DeliveryAgent assigned = shipment.getDeliveryAgent();
        if (assigned != null) {
            boolean anotherActiveShipment = assigned.getShipments().stream()
                    .anyMatch(other -> !other.getId().equals(shipment.getId()) && !isTerminal(other.getStatus()));
            if (!anotherActiveShipment) {
                assigned.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
                agents.save(assigned);
            }
        }
        Payment payment = shipment.getPayment();
        PackageEntity packageEntity = shipment.getPackageEntity();
        // Shipment owns the foreign keys to Payment and Package, so delete/flush it first.
        shipments.delete(shipment);
        shipments.flush();
        if (payment != null) payments.delete(payment);
        if (packageEntity != null) packages.delete(packageEntity);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customer(id);
        boolean hasPendingOrUndeliveredShipment = customer.getShipments().stream()
                .anyMatch(shipment -> !isTerminal(shipment.getStatus()));
        if (hasPendingOrUndeliveredShipment) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete a customer with pending or undelivered shipments");
        }
        // The requirements permit deletion once no shipment is pending/undelivered.
        // To satisfy the non-null customer foreign key, terminal shipment records and their
        // related package/payment/tracking rows are removed together with this customer.
        for (Shipment shipment : new java.util.ArrayList<>(customer.getShipments())) {
            deleteShipmentRecord(shipment);
        }
        customers.delete(customer);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        warehouse(id);
        if (shipments.existsByWarehouseId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete a warehouse while shipment records reference it");
        }
        warehouses.deleteById(id);
    }

    @Transactional
    public void deleteAgent(Long id) {
        agent(id);
        if (shipments.existsByDeliveryAgentId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete a delivery agent while shipment records reference them");
        }
        agents.deleteById(id);
    }

    @Transactional
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = payments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found: " + id));
        payment.setPaymentStatus(status);
        if (status == PaymentStatus.PAID) payment.setPaymentDateTime(LocalDateTime.now());
        return payments.save(payment);
    }

    public List<Shipment> allShipments() {
        return shipments.findAll();
    }

    private boolean isTerminal(ShipmentStatus status) {
        return status == ShipmentStatus.DELIVERED || status == ShipmentStatus.CANCELLED
                || status == ShipmentStatus.RETURNED;
    }
}
