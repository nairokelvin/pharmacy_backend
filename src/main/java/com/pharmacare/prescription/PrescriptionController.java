package com.pharmacare.prescription;

import com.pharmacare.prescription.dto.CreatePrescriptionRequest;
import com.pharmacare.sales.Sale;
import com.pharmacare.sales.SaleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionRepository prescriptions;
    private final PrescriptionStorageService storage;
    private final SaleRepository sales;

    public PrescriptionController(PrescriptionRepository prescriptions, PrescriptionStorageService storage, SaleRepository sales) {
        this.prescriptions = prescriptions;
        this.storage = storage;
        this.sales = sales;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','STORE_MANAGER')")
    public List<Prescription> list() {
        return prescriptions.findAll();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public Prescription upload(@Valid @RequestPart("data") CreatePrescriptionRequest data,
                               @RequestPart("file") MultipartFile file,
                               Authentication authentication) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Prescription file is required");
        }

        String storedFilename = storage.save(file.getBytes(), file.getOriginalFilename());

        Prescription p = new Prescription();
        p.setPatientName(data.getPatientName().trim());
        p.setDoctorName(data.getDoctorName());
        p.setIssueDate(data.getIssueDate());
        p.setPrescriptionFile(storedFilename);
        p.setCreatedBy(authentication == null ? "unknown" : authentication.getName());

        if (data.getSaleId() != null) {
            Sale sale = sales.findById(data.getSaleId()).orElseThrow();
            p.setSale(sale);
        }

        return prescriptions.save(p);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
        prescriptions.deleteById(id);
    }
}
