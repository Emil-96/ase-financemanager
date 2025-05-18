package com.emil.financemanager.service

import com.emil.financemanager.model.Budget
import com.emil.financemanager.model.Category
import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.repository.BudgetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val categoryService: CategoryService
) {
    
    fun getAllBudgets(): List<Budget> = budgetRepository.findAll()
    
    fun getBudgetById(id: Long): Budget = budgetRepository.findById(id)
        .orElseThrow { NoSuchElementException("Budget not found with id: $id") }
    
    fun getCurrentBudgets(): List<Budget> {
        val today = LocalDate.now()
        return budgetRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
    }
    
    fun getBudgetsByCategory(categoryId: Long): List<Budget> {
        val category = categoryService.getCategoryById(categoryId)
        return budgetRepository.findByCategory(category)
    }
    
    @Transactional
    fun createBudget(budget: Budget): Budget {
        return budgetRepository.save(budget)
    }
    
    @Transactional
    fun updateBudget(id: Long, budget: Budget): Budget {
        val existingBudget = getBudgetById(id)
        
        val updated = existingBudget.copy(
            name = budget.name,
            category = budget.category,
            amount = budget.amount,
            startDate = budget.startDate,
            endDate = budget.endDate,
            thresholdPercentage = budget.thresholdPercentage
        )
        
        return budgetRepository.save(updated)
    }
    
    @Transactional
    fun deleteBudget(id: Long) {
        val budget = getBudgetById(id)
        budgetRepository.delete(budget)
    }
    
    @Transactional
    fun updateBudgetWithTransaction(budgetId: Long, transaction: MoneyTransaction) {
        // This method will be called when a new transaction is created
        // It adds the transaction to the budget's transaction list
        val budget = getBudgetById(budgetId)
        budget.transactions.add(transaction)
        budgetRepository.save(budget)
    }
    
    /**
     * Calculates the spending percentage for a budget
     * @return percentage of budget spent (0-100)
     */
    fun calculateBudgetSpendingPercentage(budgetId: Long): Int {
        val budget = getBudgetById(budgetId)
        
        val totalSpent = budget.transactions
            .filter { it.type.name == "EXPENSE" }
            .sumOf { it.amount }
            
        if (budget.amount.compareTo(BigDecimal.ZERO) == 0) {
            return 0
        }
        
        return ((totalSpent.divide(budget.amount)) * BigDecimal(100)).toInt()
    }
    
    /**
     * Checks if a budget has exceeded its threshold
     */
    fun isBudgetThresholdExceeded(budgetId: Long): Boolean {
        val budget = getBudgetById(budgetId)
        val spendingPercentage = calculateBudgetSpendingPercentage(budgetId)
        
        return spendingPercentage >= budget.thresholdPercentage
    }
} 