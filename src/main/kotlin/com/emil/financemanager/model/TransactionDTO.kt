package com.emil.financemanager.model

import com.emil.financemanager.service.CategoryService
import com.emil.financemanager.service.BudgetService
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Data Transfer Object for Transaction
 * Used for form submissions and API requests
 */
class TransactionDTO {
    var id: Long? = null
    var amount: String? = null
    var description: String? = null
    var date: String? = null
    var categoryId: String? = null
    var type: String? = null
    var budgetId: String? = null
    
    fun toEntity(categoryService: CategoryService, budgetService: BudgetService?): MoneyTransaction {
        val transactionAmount = try {
            BigDecimal(amount ?: "0")
        } catch (e: NumberFormatException) {
            BigDecimal.ZERO
        }
        
        val category = categoryId?.toLongOrNull()?.let { 
            categoryService.getCategoryById(it) 
        } ?: throw IllegalArgumentException("Category is required")
        
        val budget = budgetId?.toLongOrNull()?.let { 
            budgetService?.getBudgetById(it)
        }
        
        val transactionType = try {
            if (type.isNullOrBlank()) TransactionType.EXPENSE else TransactionType.valueOf(type!!)
        } catch (e: IllegalArgumentException) {
            TransactionType.EXPENSE
        }
        
        val dateTime = try {
            val parsedDate = LocalDate.parse(date)
            LocalDateTime.of(parsedDate, LocalTime.now())
        } catch (e: Exception) {
            LocalDateTime.now()
        }
        
        return MoneyTransaction(
            id = id ?: 0,
            amount = transactionAmount,
            description = description ?: "No description",
            timestamp = dateTime,
            category = category,
            type = transactionType,
            budget = budget
        )
    }
} 