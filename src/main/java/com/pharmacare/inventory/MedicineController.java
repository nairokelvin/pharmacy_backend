package com.pharmacare.inventory;

import com.pharmacare.inventory.dto.AdjustStockRequest;
import com.pharmacare.inventory.dto.CreateMedicineRequest;
import com.pharmacare.sales.SaleItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineRepository medicines;
    private final CategoryRepository categories;
    private final StockMovementRepository movements;
    private final SaleItemRepository saleItems;

    public MedicineController(MedicineRepository medicines, CategoryRepository categories, StockMovementRepository movements, SaleItemRepository saleItems) {
        this.medicines = medicines;
        this.categories = categories;
        this.movements = movements;
        this.saleItems = saleItems;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','CASHIER','STORE_MANAGER')")
    public List<Medicine> list() {
        return medicines.findAll();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','STORE_MANAGER')")
    public List<Medicine> lowStock(@RequestParam(name = "threshold", defaultValue = "10") Integer threshold) {
        return medicines.findByQuantityInStockLessThanEqual(threshold);
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','STORE_MANAGER')")
    public List<Medicine> expiring(@RequestParam(name = "withinDays", defaultValue = "30") Integer withinDays) {
        LocalDate date = LocalDate.now().plusDays(withinDays);
        return medicines.findByExpiryDateLessThanEqual(date);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','STORE_MANAGER')")
    public Medicine create(@Valid @RequestBody CreateMedicineRequest request) {
        Medicine m = new Medicine();
        apply(m, request);
        Medicine saved = medicines.save(m);

        if (saved.getQuantityInStock() != null && saved.getQuantityInStock() != 0) {
            StockMovement mv = new StockMovement();
            mv.setMedicine(saved);
            mv.setType(StockMovementType.ADJUSTMENT);
            mv.setQuantityChange(saved.getQuantityInStock());
            mv.setReference("Initial stock (PACKAGE)");
            movements.save(mv);
        }

        if (saved.getLooseElementsInStock() != null && saved.getLooseElementsInStock() != 0) {
            StockMovement mv = new StockMovement();
            mv.setMedicine(saved);
            mv.setType(StockMovementType.ADJUSTMENT);
            mv.setQuantityChange(saved.getLooseElementsInStock());
            mv.setReference("Initial stock (ELEMENT)");
            movements.save(mv);
        }

        return saved;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','STORE_MANAGER')")
    public Medicine update(@PathVariable("id") Long id, @Valid @RequestBody CreateMedicineRequest request) {
        Medicine m = medicines.findById(id).orElseThrow(() -> new IllegalArgumentException("Medicine not found with id: " + id));
        apply(m, request);
        return medicines.save(m);
    }

    @PostMapping("/{id}/adjust-stock")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Medicine adjustStock(@PathVariable("id") Long id, @Valid @RequestBody AdjustStockRequest request) {
        Medicine m = medicines.findById(id).orElseThrow(() -> new IllegalArgumentException("Medicine not found with id: " + id));
        int newPackages = m.getQuantityInStock() + request.getPackageChange();
        int newLoose = m.getLooseElementsInStock() + request.getLooseElementsChange();
        if (newPackages < 0 || newLoose < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        m.setQuantityInStock(newPackages);
        m.setLooseElementsInStock(newLoose);
        Medicine saved = medicines.save(m);

        if (request.getPackageChange() != 0) {
            StockMovement mv = new StockMovement();
            mv.setMedicine(saved);
            mv.setType(StockMovementType.ADJUSTMENT);
            mv.setQuantityChange(request.getPackageChange());
            mv.setReference(request.getReference() == null ? null : (request.getReference() + " (PACKAGE)"));
            movements.save(mv);
        }

        if (request.getLooseElementsChange() != 0) {
            StockMovement mv = new StockMovement();
            mv.setMedicine(saved);
            mv.setType(StockMovementType.ADJUSTMENT);
            mv.setQuantityChange(request.getLooseElementsChange());
            mv.setReference(request.getReference() == null ? null : (request.getReference() + " (ELEMENT)"));
            movements.save(mv);
        }

        return saved;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(@PathVariable("id") Long id) {
        if (saleItems.existsByMedicineId(id)) {
            throw new IllegalStateException("Cannot delete medicine that is used in sales");
        }
        movements.deleteByMedicineId(id);
        medicines.deleteById(id);
    }

    private void apply(Medicine m, CreateMedicineRequest request) {
        m.setName(request.getName().trim());
        m.setBrand(request.getBrand());
        m.setBatchNumber(request.getBatchNumber());
        m.setPurchasePrice(request.getPurchasePrice());
        m.setSellingPrice(request.getSellingPrice());
        m.setSellingPricePerElement(request.getSellingPricePerElement());
        m.setElementsPerPackage(request.getElementsPerPackage());
        m.setQuantityInStock(request.getQuantityInStock());
        m.setLooseElementsInStock(request.getLooseElementsInStock());
        m.setExpiryDate(request.getExpiryDate());
        m.setBarcode(request.getBarcode());

        if (request.getCategoryId() != null) {
            Category c = categories.findById(request.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));
            m.setCategory(c);
        } else {
            m.setCategory(null);
        }
    }
}
