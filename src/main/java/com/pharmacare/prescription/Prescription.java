package com.pharmacare.prescription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pharmacare.sales.Sale;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "prescriptions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName;

    private String doctorName;

    @Column(nullable = false)
    private String prescriptionFile;

    private LocalDate issueDate;

    @Column(nullable = false)
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sale sale;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
