package com.courier.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DestinationUpdateRequest {
    @NotBlank @Size(max = 250) private String destination;
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}
