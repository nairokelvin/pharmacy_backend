package com.pharmacare.profit.dto;

import com.pharmacare.profit.ProfitSummary;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfitReport {
    private ProfitSummary summary;
    private ProfitDetail[] dailyProfits;
    private ProfitByCategory[] revenueByCategory;
    private ProfitByCategory[] expensesByCategory;
}
