package com.pharmacare.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "medicines")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category category;

    private String batchNumber;

    @Column(precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "selling_price_per_tablet", precision = 12, scale = 2)
    private BigDecimal sellingPricePerElement;

    private Integer elementsPerPackage;

    private Integer looseElementsInStock;

    @Column(nullable = false)
    private Integer quantityInStock;

    private LocalDate expiryDate;

    private String barcode;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (quantityInStock == null) {
            quantityInStock = 0;
        }
        if (looseElementsInStock == null) {
            looseElementsInStock = 0;
        }
    }

    @JsonProperty("categoryId")
    public Long getCategoryId() {
        return category == null ? null : category.getId();
    }
}
