package com.pharmacare.profit.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProfitByCategory {
    private String category;
    private BigDecimal amount;
    private Double percentage;
}
