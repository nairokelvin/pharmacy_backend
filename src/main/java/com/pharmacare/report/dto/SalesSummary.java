package com.pharmacare.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SalesSummary {
    private String period;
    private long salesCount;
    private BigDecimal totalRevenue;
}
