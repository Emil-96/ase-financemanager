package com.emil.financemanager.service

import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ReportService(
    private val transactionService: TransactionService,
    private val categoryService: CategoryService,
    private val savingGoalService: SavingGoalService
) {
    data class FinancialSummary(
        val totalIncome: BigDecimal,
        val totalExpenses: BigDecimal,
        val balance: BigDecimal,
        val incomeByCategoryMap: Map<String, BigDecimal>,
        val expensesByCategoryMap: Map<String, BigDecimal>,
        val savingGoalProgress: Map<String, Int>,
        val periodStart: String,
        val periodEnd: String
    )
    
    /**
     * Generate a financial summary for a given date range
     */
    fun generateFinancialSummary(startDate: LocalDateTime, endDate: LocalDateTime): FinancialSummary {
        // Get transactions for the period
        val transactions = transactionService.getTransactionsByDateRange(startDate, endDate)
        
        val totalIncome = transactionService.getSumByTypeAndDateRange(TransactionType.INCOME, startDate, endDate)
        val totalExpenses = transactionService.getSumByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate)
        val balance = totalIncome.subtract(totalExpenses)
        
        // Group income by category
        val incomeByCategoryMap = getCategoryAmountMap(transactions, TransactionType.INCOME)
        
        // Group expenses by category
        val expensesByCategoryMap = getCategoryAmountMap(transactions, TransactionType.EXPENSE)
        
        // Get saving goal progress
        val savingGoalProgress = savingGoalService.getAllSavingGoals()
            .associateBy { it.name }
            .mapValues { savingGoalService.calculateGoalProgress(it.value.id) }
        
        val dateFormatter = DateTimeFormatter.ISO_DATE
        
        return FinancialSummary(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            balance = balance,
            incomeByCategoryMap = incomeByCategoryMap,
            expensesByCategoryMap = expensesByCategoryMap,
            savingGoalProgress = savingGoalProgress,
            periodStart = startDate.format(dateFormatter),
            periodEnd = endDate.format(dateFormatter)
        )
    }
    
    private fun getCategoryAmountMap(transactions: List<MoneyTransaction>, type: TransactionType): Map<String, BigDecimal> {
        return transactions
            .filter { it.type == type }
            .groupBy { it.category.name }
            .mapValues { entry -> 
                entry.value.sumOf { it.amount }
            }
    }
    
    /**
     * Generate monthly summary for the current month
     */
    fun generateCurrentMonthSummary(): FinancialSummary {
        val now = LocalDateTime.now()
        val startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        return generateFinancialSummary(startOfMonth, now)
    }
    
    /**
     * Generate last month's summary
     */
    fun generateLastMonthSummary(): FinancialSummary {
        val now = LocalDateTime.now()
        val startOfLastMonth = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endOfLastMonth = now.withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59).withSecond(59)
        return generateFinancialSummary(startOfLastMonth, endOfLastMonth)
    }
    
    /**
     * Generate summary for the last x days
     */
    fun generateLastDaysSummary(days: Int): FinancialSummary {
        val now = LocalDateTime.now()
        val startDate = now.minusDays(days.toLong()).withHour(0).withMinute(0).withSecond(0).withNano(0)
        return generateFinancialSummary(startDate, now)
    }
} 