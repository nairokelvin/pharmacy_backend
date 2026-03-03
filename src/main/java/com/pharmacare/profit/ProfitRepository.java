package com.pharmacare.profit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProfitRepository extends JpaRepository<ProfitSummary, Long> {
    
    List<ProfitSummary> findByPeriodStartBetweenOrderByPeriodStartDesc(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ps FROM ProfitSummary ps WHERE ps.periodStart >= :startDate AND ps.periodEnd <= :endDate ORDER BY ps.periodStart")
    List<ProfitSummary> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ps FROM ProfitSummary ps WHERE ps.period LIKE CONCAT(:year, '%') ORDER BY ps.periodStart")
    List<ProfitSummary> findByYear(String year);
    
    @Query("SELECT ps FROM ProfitSummary ps ORDER BY ps.periodStart DESC")
    List<ProfitSummary> findAllOrderedByPeriod();
    
    @Query("SELECT SUM(ps.totalRevenue) FROM ProfitSummary ps WHERE ps.periodStart >= :startDate AND ps.periodEnd <= :endDate")
    BigDecimal findTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(ps.totalExpenses) FROM ProfitSummary ps WHERE ps.periodStart >= :startDate AND ps.periodEnd <= :endDate")
    BigDecimal findTotalExpensesByDateRange(LocalDate startDate, LocalDate endDate);
}
