package com.pharmacare.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findAllByOrderByExpenseDateDesc();
    
    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(Instant startDate, Instant endDate);
    
    List<Expense> findByCategoryOrderByExpenseDateDesc(ExpenseCategory category);
    
    @Query("SELECT e.category, SUM(e.amount) as total FROM Expense e WHERE e.expenseDate BETWEEN ?1 AND ?2 GROUP BY e.category")
    List<Object[]> findExpensesByCategoryForPeriod(Instant startDate, Instant endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN ?1 AND ?2")
    BigDecimal findTotalExpensesForPeriod(Instant startDate, Instant endDate);
    
    @Query("SELECT COUNT(e) FROM Expense e WHERE e.expenseDate BETWEEN ?1 AND ?2")
    Long countExpensesByDateRange(Instant startDate, Instant endDate);
}
