package com.pharmacare.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateMedicineRequest {

    @NotBlank
    private String name;

    private String brand;

    private Long categoryId;

    private String batchNumber;

    private BigDecimal purchasePrice;

    @NotNull
    private BigDecimal sellingPrice;

    private BigDecimal sellingPricePerElement;

    @Min(1)
    private Integer elementsPerPackage;

    @NotNull
    @Min(0)
    private Integer quantityInStock;

    @Min(0)
    private Integer looseElementsInStock;

    private LocalDate expiryDate;

    private String barcode;
}
