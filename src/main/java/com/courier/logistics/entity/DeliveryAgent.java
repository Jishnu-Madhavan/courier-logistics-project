package com.courier.logistics.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="delivery_agents")
public class DeliveryAgent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=100) private String name;
 @NotBlank @Pattern(regexp="\\d{10}", message="contact must be exactly 10 digits") @Column(nullable=false,unique=true) private String contact;
 @NotBlank @Column(nullable=false,unique=true) private String vehicleNo;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private AvailabilityStatus availabilityStatus=AvailabilityStatus.AVAILABLE;
 @NotNull @DecimalMin("0.0") @DecimalMax("5.0") private Double rating=0.0;
 @JsonIgnore @OneToMany(mappedBy="deliveryAgent") private List<Shipment> shipments=new ArrayList<>();
 public DeliveryAgent(){} public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getName(){return name;} public void setName(String v){name=v;} public String getContact(){return contact;} public void setContact(String v){contact=v;} public String getVehicleNo(){return vehicleNo;} public void setVehicleNo(String v){vehicleNo=v;} public AvailabilityStatus getAvailabilityStatus(){return availabilityStatus;} public void setAvailabilityStatus(AvailabilityStatus v){availabilityStatus=v;} public Double getRating(){return rating;} public void setRating(Double v){rating=v;} public List<Shipment> getShipments(){return shipments;} public void setShipments(List<Shipment> v){shipments=v;}
}
