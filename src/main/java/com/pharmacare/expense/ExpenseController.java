package com.pharmacare.expense;

import com.pharmacare.expense.dto.CreateExpenseRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<Expense> list() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Expense get(@PathVariable("id") Long id) {
        return expenseService.getExpenseById(id);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<Expense> getExpensesByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Instant from = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return expenseService.getExpensesByDateRange(from, to);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<Expense> getExpensesByCategory(@PathVariable("category") ExpenseCategory category) {
        return expenseService.getExpensesByCategory(category);
    }

    @GetMapping("/summary/category")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public List<Object[]> getExpensesByCategoryForPeriod(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Instant from = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return expenseService.getExpensesByCategoryForPeriod(from, to);
    }

    @GetMapping("/summary/total")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public BigDecimal getTotalExpensesForPeriod(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Instant from = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return expenseService.getTotalExpensesForPeriod(from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Expense create(@Valid @RequestBody CreateExpenseRequest request) {
        // In a real application, you'd get the username from security context
        String createdBy = "current_user"; // TODO: Get from security context
        return expenseService.createExpense(request, createdBy);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public Expense update(@PathVariable("id") Long id, @Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.updateExpense(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','STORE_MANAGER')")
    public void delete(@PathVariable("id") Long id) {
        expenseService.deleteExpense(id);
    }
}
