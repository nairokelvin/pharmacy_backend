package com.pharmacare.profit.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProfitDetail {
    private String date;
    private BigDecimal revenue;
    private BigDecimal expenses;
    private BigDecimal profit;
    private Integer salesCount;
    private Integer expenseCount;
}
