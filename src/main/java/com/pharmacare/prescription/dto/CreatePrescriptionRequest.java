package com.pharmacare.prescription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePrescriptionRequest {

    @NotBlank
    private String patientName;

    private String doctorName;

    private LocalDate issueDate;

    private Long saleId;
}
