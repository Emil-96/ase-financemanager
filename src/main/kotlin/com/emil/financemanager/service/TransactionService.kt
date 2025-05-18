package com.emil.financemanager.service

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionType
import com.emil.financemanager.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryService: CategoryService,
    private val budgetService: BudgetService
) {
    
    fun getAllTransactions(): List<MoneyTransaction> = transactionRepository.findAll()
    
    fun getTransactionById(id: Long): MoneyTransaction = transactionRepository.findById(id)
        .orElseThrow { NoSuchElementException("Transaction not found with id: $id") }
    
    fun getTransactionsByType(type: TransactionType): List<MoneyTransaction> = transactionRepository.findByType(type)
    
    fun getTransactionsByCategory(categoryId: Long): List<MoneyTransaction> {
        val category = categoryService.getCategoryById(categoryId)
        return transactionRepository.findByCategory(category)
    }
    
    fun getTransactionsByDateRange(start: LocalDateTime, end: LocalDateTime): List<MoneyTransaction> = 
        transactionRepository.findByTimestampBetween(start, end)
    
    @Transactional
    fun createTransaction(transaction: MoneyTransaction): MoneyTransaction {
        val savedTransaction = transactionRepository.save(transaction)
        
        // If this transaction is connected to a budget, update the budget
        savedTransaction.budget?.let { budget ->
            budgetService.updateBudgetWithTransaction(budget.id, savedTransaction)
        }
        
        return savedTransaction
    }
    
    @Transactional
    fun updateTransaction(id: Long, transaction: MoneyTransaction): MoneyTransaction {
        val existingTransaction = getTransactionById(id)
        
        val updated = existingTransaction.copy(
            amount = transaction.amount,
            description = transaction.description,
            category = transaction.category,
            type = transaction.type,
            budget = transaction.budget
        )
        
        return transactionRepository.save(updated)
    }
    
    @Transactional
    fun deleteTransaction(id: Long) {
        val transaction = getTransactionById(id)
        transactionRepository.delete(transaction)
    }
    
    fun getSumByType(type: TransactionType): BigDecimal = 
        transactionRepository.sumByType(type) ?: BigDecimal.ZERO
    
    fun getSumByTypeAndDateRange(type: TransactionType, start: LocalDateTime, end: LocalDateTime): BigDecimal =
        transactionRepository.sumByTypeAndTimestampBetween(type, start, end) ?: BigDecimal.ZERO
    
    fun getSumByCategoryAndDateRange(categoryId: Long, start: LocalDateTime, end: LocalDateTime): BigDecimal {
        val category = categoryService.getCategoryById(categoryId)
        return transactionRepository.sumByCategoryAndTimestampBetween(category, start, end) ?: BigDecimal.ZERO
    }
} 