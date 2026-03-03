package com.pharmacare.expense;

import com.pharmacare.expense.dto.CreateExpenseRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createExpense(CreateExpenseRequest request, String createdBy) {
        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setAmount(BigDecimal.valueOf(request.getAmount()));
        expense.setCategory(request.getCategory());
        expense.setNotes(request.getNotes());
        expense.setExpenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : Instant.now());
        expense.setReceiptNumber(request.getReceiptNumber());
        expense.setCreatedBy(createdBy);
        
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, CreateExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        expense.setDescription(request.getDescription());
        expense.setAmount(BigDecimal.valueOf(request.getAmount()));
        expense.setCategory(request.getCategory());
        expense.setNotes(request.getNotes());
        expense.setExpenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : expense.getExpenseDate());
        expense.setReceiptNumber(request.getReceiptNumber());
        
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found");
        }
        expenseRepository.deleteById(id);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByOrderByExpenseDateDesc();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public List<Expense> getExpensesByDateRange(Instant startDate, Instant endDate) {
        return expenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(startDate, endDate);
    }

    public List<Expense> getExpensesByCategory(ExpenseCategory category) {
        return expenseRepository.findByCategoryOrderByExpenseDateDesc(category);
    }

    public List<Object[]> getExpensesByCategoryForPeriod(Instant startDate, Instant endDate) {
        return expenseRepository.findExpensesByCategoryForPeriod(startDate, endDate);
    }

    public BigDecimal getTotalExpensesForPeriod(Instant startDate, Instant endDate) {
        BigDecimal total = expenseRepository.findTotalExpensesForPeriod(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
}
