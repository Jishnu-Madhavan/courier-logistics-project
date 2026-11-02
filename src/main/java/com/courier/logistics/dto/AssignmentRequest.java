package com.courier.logistics.dto;
import jakarta.validation.constraints.NotNull;
public class AssignmentRequest { @NotNull private Long deliveryAgentId; public Long getDeliveryAgentId(){return deliveryAgentId;} public void setDeliveryAgentId(Long v){deliveryAgentId=v;} }
