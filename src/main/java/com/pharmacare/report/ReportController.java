package com.pharmacare.report;

import com.pharmacare.inventory.Medicine;
import com.pharmacare.inventory.MedicineRepository;
import com.pharmacare.report.dto.SalesSummary;
import com.pharmacare.sales.Sale;
import com.pharmacare.sales.SaleRepository;
import com.pharmacare.supplier.PurchaseOrder;
import com.pharmacare.supplier.PurchaseOrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final SaleRepository sales;
    private final MedicineRepository medicines;
    private final PurchaseOrderRepository purchaseOrders;

    public ReportController(SaleRepository sales, MedicineRepository medicines, PurchaseOrderRepository purchaseOrders) {
        this.sales = sales;
        this.medicines = medicines;
        this.purchaseOrders = purchaseOrders;
    }

    @GetMapping("/daily-sales")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public SalesSummary dailySales(@RequestParam(name = "date", required = false) LocalDate date) {
        LocalDate d = date == null ? LocalDate.now() : date;
        Instant from = d.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = d.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<Sale> list = sales.findByCreatedAtBetween(from, to);
        BigDecimal total = list.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SalesSummary(d.toString(), list.size(), total);
    }

    @GetMapping("/daily-sales-detail")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<Sale> dailySalesDetail(@RequestParam(name = "date", required = false) LocalDate date) {
        LocalDate d = date == null ? LocalDate.now() : date;
        Instant from = d.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = d.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return sales.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
    }

    @GetMapping("/monthly-sales")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public SalesSummary monthlySales(@RequestParam(name = "year") int year, @RequestParam(name = "month") int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        Instant from = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = end.atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<Sale> list = sales.findByCreatedAtBetween(from, to);
        BigDecimal total = list.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SalesSummary(year + "-" + String.format("%02d", month), list.size(), total);
    }

    @GetMapping("/monthly-sales-detail")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<Sale> monthlySalesDetail(@RequestParam(name = "year") int year, @RequestParam(name = "month") int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        Instant from = start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = end.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return sales.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
    }

    @GetMapping("/date-range-sales")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public SalesSummary dateRangeSales(@RequestParam(name = "startDate") LocalDate startDate, @RequestParam(name = "endDate") LocalDate endDate) {
        Instant from = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<Sale> list = sales.findByCreatedAtBetween(from, to);
        BigDecimal total = list.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SalesSummary(startDate + " to " + endDate, list.size(), total);
    }

    @GetMapping("/date-range-sales-detail")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<Sale> dateRangeSalesDetail(@RequestParam(name = "startDate") LocalDate startDate, @RequestParam(name = "endDate") LocalDate endDate) {
        Instant from = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return sales.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER','PHARMACIST')")
    public List<Medicine> lowStock(@RequestParam(name = "threshold", defaultValue = "10") Integer threshold) {
        return medicines.findByQuantityInStockLessThanEqual(threshold);
    }

    @GetMapping("/expiry")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER','PHARMACIST')")
    public List<Medicine> expiry(@RequestParam(name = "withinDays", defaultValue = "90") Integer withinDays) {
        LocalDate date = LocalDate.now().plusDays(withinDays);
        return medicines.findByExpiryDateLessThanEqual(date);
    }

    @GetMapping("/supplier-purchases")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<PurchaseOrder> supplierPurchases() {
        return purchaseOrders.findAll();
    }
}
