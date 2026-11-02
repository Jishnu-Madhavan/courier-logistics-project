package com.courier.logistics.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import jakarta.validation.constraints.*; import java.time.LocalDateTime;
@Entity @Table(name="tracking_history")
public class TrackingHistory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank private String location;
 @NotNull @Column(name="event_timestamp", nullable=false) private LocalDateTime timestamp;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ShipmentStatus status;
 @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="shipment_id",nullable=false) private Shipment shipment;
 public TrackingHistory(){} public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getLocation(){return location;} public void setLocation(String v){location=v;} public LocalDateTime getTimestamp(){return timestamp;} public void setTimestamp(LocalDateTime v){timestamp=v;} public ShipmentStatus getStatus(){return status;} public void setStatus(ShipmentStatus v){status=v;} public Shipment getShipment(){return shipment;} public void setShipment(Shipment v){shipment=v;}
}
