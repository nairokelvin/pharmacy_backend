package com.pharmacare.sales.dto;

import com.pharmacare.sales.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateSaleRequest {

    private String customerName;

    @NotNull
    private BigDecimal taxAmount;

    @NotNull
    private BigDecimal discount;

    @NotNull
    private PaymentMethod paymentMethod;

    @Valid
    @NotNull
    private List<SaleItemRequest> items;
}
