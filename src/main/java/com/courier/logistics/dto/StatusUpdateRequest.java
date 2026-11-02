package com.courier.logistics.dto;
import com.courier.logistics.entity.ShipmentStatus; import jakarta.validation.constraints.NotNull;
public class StatusUpdateRequest { @NotNull private ShipmentStatus status; private String location; public ShipmentStatus getStatus(){return status;} public void setStatus(ShipmentStatus v){status=v;} public String getLocation(){return location;} public void setLocation(String v){location=v;} }
