package com.pharmacare.inventory;

import com.pharmacare.inventory.dto.CreateCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categories;

    public CategoryController(CategoryRepository categories) {
        this.categories = categories;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','STORE_MANAGER')")
    public List<Category> list() {
        return categories.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Category create(@Valid @RequestBody CreateCategoryRequest request) {
        if (categories.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category already exists");
        }
        Category c = new Category();
        c.setName(request.getName().trim());
        return categories.save(c);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Category update(@PathVariable("id") Long id, @Valid @RequestBody CreateCategoryRequest request) {
        Category c = categories.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        // Check if another category with the same name already exists
        if (categories.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        
        c.setName(request.getName().trim());
        return categories.save(c);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
        categories.deleteById(id);
    }
}
