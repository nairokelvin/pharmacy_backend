package com.pharmacare.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sales")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    private String customerName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
        if (taxAmount == null) {
            taxAmount = BigDecimal.ZERO;
        }
        if (discount == null) {
            discount = BigDecimal.ZERO;
        }
    }
}
