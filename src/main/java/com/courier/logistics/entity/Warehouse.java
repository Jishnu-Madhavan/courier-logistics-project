package com.courier.logistics.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import jakarta.validation.constraints.*; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="warehouses")
public class Warehouse {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=120) private String name;
 @NotBlank @Size(max=250) private String location;
 @NotNull @PositiveOrZero private Integer capacity;
 @NotBlank @Pattern(regexp="\\d{10}", message="contact must be exactly 10 digits") @Column(nullable=false,unique=true) private String contact;
 @JsonIgnore @OneToMany(mappedBy="warehouse") private List<Shipment> shipments=new ArrayList<>();
 public Warehouse(){} public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getName(){return name;} public void setName(String v){name=v;} public String getLocation(){return location;} public void setLocation(String v){location=v;} public Integer getCapacity(){return capacity;} public void setCapacity(Integer v){capacity=v;} public String getContact(){return contact;} public void setContact(String v){contact=v;} public List<Shipment> getShipments(){return shipments;} public void setShipments(List<Shipment> v){shipments=v;}
}
