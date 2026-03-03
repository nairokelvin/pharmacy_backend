package com.pharmacare.profit;

import com.pharmacare.profit.dto.ProfitByCategory;
import com.pharmacare.profit.dto.ProfitDetail;
import com.pharmacare.profit.dto.ProfitReport;
import com.pharmacare.profit.dto.ProductProfitDetail;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profit")
public class ProfitController {

    private final ProfitService profitService;

    public ProfitController(ProfitService profitService) {
        this.profitService = profitService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public ProfitSummary getProfitSummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return profitService.calculateProfitSummary(startDate, endDate);
    }

    @GetMapping("/daily")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<ProfitDetail> getDailyProfits(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return profitService.calculateDailyProfits(startDate, endDate);
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public ProfitReport getProfitReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ProfitSummary summary = profitService.calculateProfitSummary(startDate, endDate);
        List<ProfitDetail> dailyProfits = profitService.calculateDailyProfits(startDate, endDate);
        List<ProfitByCategory> revenueByCategory = profitService.getRevenueByCategory(startDate, endDate);
        List<ProfitByCategory> expensesByCategory = profitService.getExpensesByCategory(startDate, endDate);
        
        ProfitReport report = new ProfitReport();
        report.setSummary(summary);
        report.setDailyProfits(dailyProfits.toArray(new ProfitDetail[0]));
        report.setRevenueByCategory(revenueByCategory.toArray(new ProfitByCategory[0]));
        report.setExpensesByCategory(expensesByCategory.toArray(new ProfitByCategory[0]));
        
        return report;
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<ProfitDetail> getMonthlyProfits(@RequestParam("year") int year) {
        return profitService.calculateMonthlyProfits(year);
    }

    @GetMapping("/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<ProfitDetail> getYearlyProfits() {
        return profitService.calculateYearlyProfits();
    }

    @GetMapping("/revenue-by-category")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<ProfitByCategory> getRevenueByCategory(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return profitService.getRevenueByCategory(startDate, endDate);
    }

    @GetMapping("/expenses-by-category")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<ProfitByCategory> getExpensesByCategory(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return profitService.getExpensesByCategory(startDate, endDate);
    }

    @GetMapping("/product-profit")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<ProductProfitDetail> getProductProfitByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return profitService.getProductProfitByDateRange(startDate, endDate);
    }
}
