package com.pharmacare.profit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "profit_summaries")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProfitSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "total_revenue", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "total_expenses", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalExpenses;

    @Column(name = "gross_profit", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossProfit;

    @Column(name = "net_profit", nullable = false, precision = 15, scale = 2)
    private BigDecimal netProfit;

    @Column(name = "profit_margin", nullable = false, precision = 5, scale = 4)
    private BigDecimal profitMargin;

    @Column(name = "sales_count", nullable = false)
    private Integer salesCount;

    @Column(name = "expense_count", nullable = false)
    private Integer expenseCount;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}
