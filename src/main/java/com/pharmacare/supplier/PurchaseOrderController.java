package com.pharmacare.supplier;

import com.pharmacare.supplier.dto.CreatePurchaseOrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderRepository orders;
    private final SupplierRepository suppliers;

    public PurchaseOrderController(PurchaseOrderRepository orders, SupplierRepository suppliers) {
        this.orders = orders;
        this.suppliers = suppliers;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER','PHARMACIST')")
    public List<PurchaseOrder> list() {
        return orders.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public PurchaseOrder create(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        Supplier supplier = suppliers.findById(request.getSupplierId()).orElseThrow();
        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setTotalAmount(request.getTotalAmount());
        po.setStatus(PurchaseOrderStatus.ORDERED);
        return orders.save(po);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public PurchaseOrder updateStatus(@PathVariable("id") Long id, @RequestParam("status") PurchaseOrderStatus status) {
        PurchaseOrder po = orders.findById(id).orElseThrow();
        po.setStatus(status);
        return orders.save(po);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
        orders.deleteById(id);
    }
}
