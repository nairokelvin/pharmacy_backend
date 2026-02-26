package com.pharmacare.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByQuantityInStockLessThanEqual(Integer threshold);
    List<Medicine> findByExpiryDateLessThanEqual(LocalDate date);
}
