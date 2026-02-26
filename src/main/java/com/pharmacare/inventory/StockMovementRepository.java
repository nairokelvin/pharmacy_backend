package com.pharmacare.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    long countByMedicineId(Long medicineId);
    void deleteByMedicineId(Long medicineId);
}
