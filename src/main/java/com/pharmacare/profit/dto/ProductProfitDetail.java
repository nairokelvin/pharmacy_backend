package com.pharmacare.profit.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductProfitDetail {
    private String date;
    private Long productId;
    private String productName;
    private String category;
    private Integer packagesSold;
    private Integer elementsSold;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal profit;
    private BigDecimal profitMargin;
}
