package com.pharmacare.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    List<Sale> findByCreatedAtBetween(Instant from, Instant to);
    
    List<Sale> findAllByOrderByCreatedAtDesc();
    
    List<Sale> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to);
    
    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal findTotalRevenueByDateRange(Instant startDate, Instant endDate);
    
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    Long countSalesByDateRange(Instant startDate, Instant endDate);
    
    @Query("SELECT m.category.name, SUM(si.quantity * si.price) FROM SaleItem si JOIN si.medicine m WHERE si.sale.createdAt BETWEEN :startDate AND :endDate GROUP BY m.category.name")
    List<Object[]> findRevenueByCategoryForPeriod(Instant startDate, Instant endDate);
    
    @Query("SELECT si.sale.createdAt, m.id, m.name, m.category.name, SUM(CASE WHEN si.unitType = 'PACKAGE' THEN si.quantity ELSE 0 END), SUM(CASE WHEN si.unitType = 'ELEMENT' THEN si.quantity ELSE 0 END), SUM(si.quantity * si.price), SUM(CASE WHEN si.unitType = 'PACKAGE' THEN si.quantity * COALESCE(m.purchasePrice, 0) ELSE si.quantity * COALESCE(m.purchasePrice, 0) / NULLIF(m.elementsPerPackage, 1) END) FROM SaleItem si JOIN si.medicine m WHERE si.sale.createdAt BETWEEN :startDate AND :endDate GROUP BY si.sale.createdAt, m.id, m.name, m.category.name ORDER BY si.sale.createdAt DESC, SUM(si.quantity * si.price) DESC")
    List<Object[]> findProductProfitByDateRange(Instant startDate, Instant endDate);
    
    @Query("SELECT SUM(CASE WHEN si.unitType = 'PACKAGE' THEN si.quantity * COALESCE(m.purchasePrice, 0) ELSE si.quantity * COALESCE(m.purchasePrice, 0) / NULLIF(m.elementsPerPackage, 1) END) FROM SaleItem si JOIN si.medicine m WHERE si.sale.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal findCostOfGoodsForPeriod(Instant startDate, Instant endDate);
}
