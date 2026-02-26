package com.pharmacare.sales.dto;

import com.pharmacare.sales.SaleUnitType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaleItemRequest {

    @NotNull
    private Long medicineId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private BigDecimal price;

    @NotNull
    private SaleUnitType unitType;
}
