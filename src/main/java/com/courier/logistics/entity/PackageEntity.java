package com.courier.logistics.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import jakarta.validation.constraints.*;
@Entity @Table(name="packages")
public class PackageEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=80) private String packageType;
 private Boolean fragile=false;
 @NotNull @Positive private Double length; @NotNull @Positive private Double breadth; @NotNull @Positive private Double height;
 @JsonIgnore @OneToOne(mappedBy="packageEntity") private Shipment shipment;
 public PackageEntity(){} public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getPackageType(){return packageType;} public void setPackageType(String v){packageType=v;} public Boolean getFragile(){return fragile;} public void setFragile(Boolean v){fragile=v;} public Double getLength(){return length;} public void setLength(Double v){length=v;} public Double getBreadth(){return breadth;} public void setBreadth(Double v){breadth=v;} public Double getHeight(){return height;} public void setHeight(Double v){height=v;} public Shipment getShipment(){return shipment;} public void setShipment(Shipment v){shipment=v;}
}
