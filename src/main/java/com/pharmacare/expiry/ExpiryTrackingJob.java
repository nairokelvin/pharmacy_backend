package com.pharmacare.expiry;

import com.pharmacare.inventory.Medicine;
import com.pharmacare.inventory.MedicineRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExpiryTrackingJob {

    private final MedicineRepository medicines;

    public ExpiryTrackingJob(MedicineRepository medicines) {
        this.medicines = medicines;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkExpiredMedicinesDaily() {
        LocalDate today = LocalDate.now();
        List<Medicine> expired = medicines.findByExpiryDateLessThanEqual(today);
        expired.size();
    }
}
