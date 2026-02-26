package com.pharmacare.sales;

import com.pharmacare.sales.dto.CreateSaleRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleRepository sales;
    private final SaleService saleService;

    public SaleController(SaleRepository sales, SaleService saleService) {
        this.sales = sales;
        this.saleService = saleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CASHIER','PHARMACIST','STORE_MANAGER')")
    public List<Sale> list() {
        return sales.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CASHIER','PHARMACIST','STORE_MANAGER')")
    public Sale get(@PathVariable("id") Long id) {
        return sales.findById(id).orElseThrow();
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('ADMIN','CASHIER','PHARMACIST','STORE_MANAGER')")
    public List<SaleItem> items(@PathVariable("id") Long id) {
        return saleService.getItems(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CASHIER')")
    public Sale create(@Valid @RequestBody CreateSaleRequest request) {
        return saleService.createSale(request);
    }
}
