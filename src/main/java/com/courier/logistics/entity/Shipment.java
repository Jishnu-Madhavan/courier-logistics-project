package com.courier.logistics.entity;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="shipments")
public class Shipment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Column(nullable=false,unique=true) private String trackingNumber;
 @NotBlank private String source; @NotBlank private String destination;
 @NotNull @Positive private Double weight;
 @Column(nullable=false) private LocalDateTime shipmentDateTime=LocalDateTime.now();
 private LocalDate deliveryDate;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ShipmentStatus status=ShipmentStatus.ORDER_PLACED;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="customer_id",nullable=false) private Customer customer;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="delivery_agent_id",nullable=false) private DeliveryAgent deliveryAgent;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="warehouse_id",nullable=false) private Warehouse warehouse;
 @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE}) @JoinColumn(name="payment_id",nullable=false) private Payment payment;
 @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE}) @JoinColumn(name="package_id",nullable=false) private PackageEntity packageEntity;
 @OneToMany(mappedBy="shipment",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("timestamp ASC") private List<TrackingHistory> trackingHistory=new ArrayList<>();
 public Shipment(){} public Long getId(){return id;} public void setId(Long v){id=v;} public String getTrackingNumber(){return trackingNumber;} public void setTrackingNumber(String v){trackingNumber=v;} public String getSource(){return source;} public void setSource(String v){source=v;} public String getDestination(){return destination;} public void setDestination(String v){destination=v;} public Double getWeight(){return weight;} public void setWeight(Double v){weight=v;} public LocalDateTime getShipmentDateTime(){return shipmentDateTime;} public void setShipmentDateTime(LocalDateTime v){shipmentDateTime=v;} public LocalDate getDeliveryDate(){return deliveryDate;} public void setDeliveryDate(LocalDate v){deliveryDate=v;} public ShipmentStatus getStatus(){return status;} public void setStatus(ShipmentStatus v){status=v;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;} public DeliveryAgent getDeliveryAgent(){return deliveryAgent;} public void setDeliveryAgent(DeliveryAgent v){deliveryAgent=v;} public Warehouse getWarehouse(){return warehouse;} public void setWarehouse(Warehouse v){warehouse=v;} public Payment getPayment(){return payment;} public void setPayment(Payment v){payment=v;} public PackageEntity getPackageEntity(){return packageEntity;} public void setPackageEntity(PackageEntity v){packageEntity=v;} public List<TrackingHistory> getTrackingHistory(){return trackingHistory;} public void setTrackingHistory(List<TrackingHistory> v){trackingHistory=v;}
}
