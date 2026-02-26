package com.pharmacare.supplier;

import com.pharmacare.supplier.dto.CreateSupplierRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierRepository suppliers;

    public SupplierController(SupplierRepository suppliers) {
        this.suppliers = suppliers;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER','PHARMACIST')")
    public List<Supplier> list() {
        return suppliers.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Supplier create(@Valid @RequestBody CreateSupplierRequest request) {
        Supplier s = new Supplier();
        s.setName(request.getName().trim());
        s.setPhone(request.getPhone());
        s.setEmail(request.getEmail());
        s.setAddress(request.getAddress());
        return suppliers.save(s);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Supplier update(@PathVariable("id") Long id, @Valid @RequestBody CreateSupplierRequest request) {
        Supplier s = suppliers.findById(id).orElseThrow();
        s.setName(request.getName().trim());
        s.setPhone(request.getPhone());
        s.setEmail(request.getEmail());
        s.setAddress(request.getAddress());
        return suppliers.save(s);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
        suppliers.deleteById(id);
    }
}
