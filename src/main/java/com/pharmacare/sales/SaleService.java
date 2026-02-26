package com.pharmacare.sales;

import com.pharmacare.inventory.Medicine;
import com.pharmacare.inventory.MedicineRepository;
import com.pharmacare.inventory.StockMovement;
import com.pharmacare.inventory.StockMovementRepository;
import com.pharmacare.inventory.StockMovementType;
import com.pharmacare.sales.dto.CreateSaleRequest;
import com.pharmacare.sales.dto.SaleItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SaleService {

    private final SaleRepository sales;
    private final SaleItemRepository saleItems;
    private final MedicineRepository medicines;
    private final StockMovementRepository movements;

    public SaleService(SaleRepository sales, SaleItemRepository saleItems, MedicineRepository medicines, StockMovementRepository movements) {
        this.sales = sales;
        this.saleItems = saleItems;
        this.medicines = medicines;
        this.movements = movements;
    }

    @Transactional
    public Sale createSale(CreateSaleRequest request) {
        String invoice = generateInvoiceNumber();

        Sale sale = new Sale();
        sale.setInvoiceNumber(invoice);
        sale.setCustomerName(request.getCustomerName());
        sale.setTaxAmount(request.getTaxAmount());
        sale.setDiscount(request.getDiscount());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setCreatedAt(Instant.now());

        BigDecimal itemsTotal = BigDecimal.ZERO;
        List<SaleItem> createdItems = new ArrayList<>();

        for (SaleItemRequest itemReq : request.getItems()) {
            Medicine med = medicines.findById(itemReq.getMedicineId()).orElseThrow();

            if (itemReq.getUnitType() == SaleUnitType.PACKAGE) {
                int newPackages = med.getQuantityInStock() - itemReq.getQuantity();
                if (newPackages < 0) {
                    throw new IllegalArgumentException("Not enough packages in stock for medicine id=" + med.getId());
                }
                med.setQuantityInStock(newPackages);
                medicines.save(med);
            } else if (itemReq.getUnitType() == SaleUnitType.ELEMENT) {
                if (med.getElementsPerPackage() == null || med.getElementsPerPackage() <= 0) {
                    throw new IllegalArgumentException("elementsPerPackage is required to sell by element for medicine id=" + med.getId());
                }

                int packages = med.getQuantityInStock();
                int loose = med.getLooseElementsInStock() == null ? 0 : med.getLooseElementsInStock();
                int needed = itemReq.getQuantity();

                long totalElements = (long) loose + ((long) packages * (long) med.getElementsPerPackage());
                if (totalElements < needed) {
                    throw new IllegalArgumentException("Not enough elements in stock for medicine id=" + med.getId());
                }

                while (loose < needed) {
                    if (packages <= 0) {
                        throw new IllegalArgumentException("Not enough elements in stock for medicine id=" + med.getId());
                    }
                    packages -= 1;
                    loose += med.getElementsPerPackage();
                }

                loose -= needed;
                med.setQuantityInStock(packages);
                med.setLooseElementsInStock(loose);
                medicines.save(med);
            } else {
                throw new IllegalArgumentException("Invalid unitType");
            }

            BigDecimal subtotal = itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            itemsTotal = itemsTotal.add(subtotal);

            SaleItem si = new SaleItem();
            si.setSale(sale);
            si.setMedicine(med);
            si.setQuantity(itemReq.getQuantity());
            si.setUnitType(itemReq.getUnitType());
            si.setPrice(itemReq.getPrice());
            si.setSubtotal(subtotal);
            createdItems.add(si);

            StockMovement mv = new StockMovement();
            mv.setMedicine(med);
            mv.setType(StockMovementType.SALE);
            mv.setQuantityChange(-itemReq.getQuantity());
            mv.setReference("Sale:" + invoice + " (" + itemReq.getUnitType() + ")");
            movements.save(mv);
        }

        BigDecimal total = itemsTotal.add(request.getTaxAmount()).subtract(request.getDiscount());
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        sale.setTotalAmount(total);

        Sale savedSale = sales.save(sale);
        for (SaleItem si : createdItems) {
            si.setSale(savedSale);
            saleItems.save(si);
        }

        return savedSale;
    }

    public List<SaleItem> getItems(Long saleId) {
        return saleItems.findBySaleId(saleId);
    }

    private String generateInvoiceNumber() {
        String candidate = "INV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        while (sales.existsByInvoiceNumber(candidate)) {
            candidate = "INV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        }
        return candidate;
    }
}
