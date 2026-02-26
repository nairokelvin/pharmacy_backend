package com.pharmacare.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    boolean existsByInvoiceNumber(String invoiceNumber);
    List<Sale> findByCreatedAtBetween(Instant from, Instant to);
}
