package com.courier.logistics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="customers")
public class Customer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=100) private String name;
 @NotBlank @Email @Column(nullable=false, unique=true) private String email;
 @NotBlank @Pattern(regexp="\\d{10}", message="contact must be exactly 10 digits") @Column(nullable=false, unique=true) private String contact;
 @NotBlank @Size(max=500) private String address;
 @JsonIgnore @OneToMany(mappedBy="customer") private List<Shipment> shipments = new ArrayList<>();
 public Customer() {}
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getName(){return name;} public void setName(String name){this.name=name;}
 public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
 public String getContact(){return contact;} public void setContact(String contact){this.contact=contact;}
 public String getAddress(){return address;} public void setAddress(String address){this.address=address;}
 public List<Shipment> getShipments(){return shipments;} public void setShipments(List<Shipment> shipments){this.shipments=shipments;}
}
