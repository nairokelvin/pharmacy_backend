package com.pharmacare.supplier.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePurchaseOrderRequest {

    @NotNull
    private Long supplierId;

    @NotNull
    private BigDecimal totalAmount;
}
