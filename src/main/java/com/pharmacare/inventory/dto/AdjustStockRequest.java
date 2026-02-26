package com.pharmacare.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustStockRequest {

    @NotNull
    private Integer packageChange;

    @NotNull
    private Integer looseElementsChange;

    private String reference;
}
