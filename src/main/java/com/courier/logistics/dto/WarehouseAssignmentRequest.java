package com.courier.logistics.dto;

import jakarta.validation.constraints.NotNull;

public class WarehouseAssignmentRequest {
    @NotNull private Long warehouseId;
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
}
