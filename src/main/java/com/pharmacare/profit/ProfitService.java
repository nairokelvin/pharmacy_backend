package com.pharmacare.profit;

import com.pharmacare.expense.ExpenseRepository;
import com.pharmacare.profit.dto.ProfitByCategory;
import com.pharmacare.profit.dto.ProfitDetail;
import com.pharmacare.profit.dto.ProductProfitDetail;
import com.pharmacare.sales.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfitService {

    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;

    public ProfitService(SaleRepository saleRepository, ExpenseRepository expenseRepository) {
        this.saleRepository = saleRepository;
        this.expenseRepository = expenseRepository;
    }

    public ProfitSummary calculateProfitSummary(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalRevenue = saleRepository.findTotalRevenueByDateRange(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        BigDecimal totalExpenses = expenseRepository.findTotalExpensesForPeriod(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        
        // Calculate cost of goods sold for the period
        BigDecimal totalCostOfGoods = calculateCostOfGoodsForPeriod(startDate, endDate);
        
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;
        if (totalCostOfGoods == null) totalCostOfGoods = BigDecimal.ZERO;
        
        // Net profit = (Revenue - Cost of Goods) - Expenses
        BigDecimal grossProfit = totalRevenue.subtract(totalCostOfGoods);
        BigDecimal netProfit = grossProfit.subtract(totalExpenses);
        BigDecimal profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
            netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        Long salesCount = saleRepository.countSalesByDateRange(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        Long expenseCount = expenseRepository.countExpensesByDateRange(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        
        String period = startDate + " - " + endDate;
        
        ProfitSummary summary = new ProfitSummary();
        summary.setPeriod(period);
        summary.setTotalRevenue(totalRevenue);
        summary.setTotalExpenses(totalExpenses);
        summary.setGrossProfit(grossProfit);
        summary.setNetProfit(netProfit);
        summary.setProfitMargin(profitMargin);
        summary.setSalesCount(salesCount.intValue());
        summary.setExpenseCount(expenseCount.intValue());
        summary.setPeriodStart(startDate);
        summary.setPeriodEnd(endDate);
        summary.setCreatedAt(LocalDate.now());
        
        return summary;
    }

    public List<ProfitDetail> calculateDailyProfits(LocalDate startDate, LocalDate endDate) {
        List<ProfitDetail> dailyProfits = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            LocalDate nextDay = current.plusDays(1);
            
            BigDecimal dailyRevenue = saleRepository.findTotalRevenueByDateRange(
                current.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            BigDecimal dailyExpenses = expenseRepository.findTotalExpensesForPeriod(
                current.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            // Calculate cost of goods sold for the day
            BigDecimal dailyCostOfGoods = calculateCostOfGoodsForPeriod(current, nextDay.minusDays(1));
            
            if (dailyRevenue == null) dailyRevenue = BigDecimal.ZERO;
            if (dailyExpenses == null) dailyExpenses = BigDecimal.ZERO;
            if (dailyCostOfGoods == null) dailyCostOfGoods = BigDecimal.ZERO;
            
            // Net profit = (Revenue - Cost of Goods) - Expenses
            BigDecimal grossProfit = dailyRevenue.subtract(dailyCostOfGoods);
            BigDecimal dailyProfit = grossProfit.subtract(dailyExpenses);
            
            Long salesCount = saleRepository.countSalesByDateRange(
                current.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            Long expenseCount = expenseRepository.countExpensesByDateRange(
                current.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            ProfitDetail detail = new ProfitDetail();
            detail.setDate(current.toString());
            detail.setRevenue(dailyRevenue);
            detail.setExpenses(dailyExpenses);
            detail.setProfit(dailyProfit);
            detail.setSalesCount(salesCount.intValue());
            detail.setExpenseCount(expenseCount.intValue());
            
            dailyProfits.add(detail);
            current = nextDay;
        }
        
        return dailyProfits;
    }

    public List<ProfitDetail> calculateMonthlyProfits(int year) {
        List<ProfitDetail> monthlyProfits = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            
            BigDecimal monthlyRevenue = saleRepository.findTotalRevenueByDateRange(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            BigDecimal monthlyExpenses = expenseRepository.findTotalExpensesForPeriod(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            // Calculate cost of goods sold for the month
            BigDecimal monthlyCostOfGoods = calculateCostOfGoodsForPeriod(startDate, endDate);
            
            if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;
            if (monthlyExpenses == null) monthlyExpenses = BigDecimal.ZERO;
            if (monthlyCostOfGoods == null) monthlyCostOfGoods = BigDecimal.ZERO;
            
            // Net profit = (Revenue - Cost of Goods) - Expenses
            BigDecimal grossProfit = monthlyRevenue.subtract(monthlyCostOfGoods);
            BigDecimal netProfit = grossProfit.subtract(monthlyExpenses);
            
            Long salesCount = saleRepository.countSalesByDateRange(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            Long expenseCount = expenseRepository.countExpensesByDateRange(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            ProfitDetail detail = new ProfitDetail();
            detail.setDate(yearMonth.toString());
            detail.setRevenue(monthlyRevenue);
            detail.setExpenses(monthlyExpenses);
            detail.setProfit(netProfit);
            detail.setSalesCount(salesCount.intValue());
            detail.setExpenseCount(expenseCount.intValue());
            
            monthlyProfits.add(detail);
        }
        
        return monthlyProfits;
    }

    public List<ProfitDetail> calculateYearlyProfits() {
        List<ProfitDetail> yearlyProfits = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        
        for (int year = currentYear - 4; year <= currentYear; year++) {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);
            
            BigDecimal yearlyRevenue = saleRepository.findTotalRevenueByDateRange(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            BigDecimal yearlyExpenses = expenseRepository.findTotalExpensesForPeriod(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            // Calculate cost of goods sold for the year
            BigDecimal yearlyCostOfGoods = calculateCostOfGoodsForPeriod(startDate, endDate);
            
            if (yearlyRevenue == null) yearlyRevenue = BigDecimal.ZERO;
            if (yearlyExpenses == null) yearlyExpenses = BigDecimal.ZERO;
            if (yearlyCostOfGoods == null) yearlyCostOfGoods = BigDecimal.ZERO;
            
            // Net profit = (Revenue - Cost of Goods) - Expenses
            BigDecimal grossProfit = yearlyRevenue.subtract(yearlyCostOfGoods);
            BigDecimal netProfit = grossProfit.subtract(yearlyExpenses);
            
            Long salesCount = saleRepository.countSalesByDateRange(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            Long expenseCount = expenseRepository.countExpensesByDateRange(
                startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            ProfitDetail detail = new ProfitDetail();
            detail.setDate(String.valueOf(year));
            detail.setRevenue(yearlyRevenue);
            detail.setExpenses(yearlyExpenses);
            detail.setProfit(netProfit);
            detail.setSalesCount(salesCount.intValue());
            detail.setExpenseCount(expenseCount.intValue());
            
            yearlyProfits.add(detail);
        }
        
        return yearlyProfits;
    }

    public List<ProfitByCategory> getRevenueByCategory(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = saleRepository.findRevenueByCategoryForPeriod(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        return convertToProfitByCategory(results);
    }

    public List<ProfitByCategory> getExpensesByCategory(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = expenseRepository.findExpensesByCategoryForPeriod(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        return convertToProfitByCategory(results);
    }

    private List<ProfitByCategory> convertToProfitByCategory(List<Object[]> results) {
        List<ProfitByCategory> categories = new ArrayList<>();
        BigDecimal total = results.stream()
            .map(row -> (BigDecimal) row[1])
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        for (Object[] row : results) {
            String category = row[0] != null ? row[0].toString() : "Unknown";
            BigDecimal amount = (BigDecimal) row[1];
            BigDecimal percentage = total.compareTo(BigDecimal.ZERO) > 0 ? 
                amount.divide(total, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            
            ProfitByCategory profitByCategory = new ProfitByCategory();
            profitByCategory.setCategory(category);
            profitByCategory.setAmount(amount);
            profitByCategory.setPercentage(percentage.doubleValue());
            
            categories.add(profitByCategory);
        }
        
        return categories;
    }

    public List<ProductProfitDetail> getProductProfitByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = saleRepository.findProductProfitByDateRange(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        
        List<ProductProfitDetail> productProfits = new ArrayList<>();
        
        for (Object[] row : results) {
            ProductProfitDetail detail = new ProductProfitDetail();
            detail.setDate(((java.time.Instant) row[0]).atZone(ZoneId.systemDefault()).toLocalDate().toString());
            detail.setProductId((Long) row[1]);
            detail.setProductName((String) row[2]);
            detail.setCategory((String) row[3]);
            detail.setPackagesSold(((Number) row[4]).intValue());
            detail.setElementsSold(((Number) row[5]).intValue());
            detail.setRevenue((BigDecimal) row[6]);
            detail.setCost((BigDecimal) row[7]);
            
            BigDecimal profit = detail.getRevenue().subtract(detail.getCost());
            detail.setProfit(profit);
            
            BigDecimal profitMargin = detail.getRevenue().compareTo(BigDecimal.ZERO) > 0 ? 
                profit.divide(detail.getRevenue(), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            detail.setProfitMargin(profitMargin);
            
            productProfits.add(detail);
        }
        
        return productProfits;
    }

    private BigDecimal calculateCostOfGoodsForPeriod(LocalDate startDate, LocalDate endDate) {
        // Query to calculate cost of goods sold for the period
        BigDecimal result = saleRepository.findCostOfGoodsForPeriod(
            startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
            endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        
        return result != null ? result : BigDecimal.ZERO;
    }
}
