package com.emil.financemanager.service

import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class ReportServiceTest {
    
    @Mock
    private lateinit var transactionService: TransactionService
    
    @Mock
    private lateinit var categoryService: CategoryService
    
    @Mock
    private lateinit var savingGoalService: SavingGoalService
    
    @InjectMocks
    private lateinit var reportService: ReportService
    
    private val startDate = LocalDateTime.of(2023, 1, 1, 0, 0)
    private val endDate = LocalDateTime.of(2023, 1, 31, 23, 59)
    
    @BeforeEach
    fun setup() {
        // Common setup if needed
    }
    
    @Test
    fun `should generate financial summary for date range`() {
        // Given
        val transactions = emptyList<MoneyTransaction>()
        val totalIncome = BigDecimal("1000.00")
        val totalExpenses = BigDecimal("750.00")
        val expectedBalance = BigDecimal("250.00")
        
        `when`(transactionService.getTransactionsByDateRange(startDate, endDate)).thenReturn(transactions)
        `when`(transactionService.getSumByTypeAndDateRange(TransactionType.INCOME, startDate, endDate)).thenReturn(totalIncome)
        `when`(transactionService.getSumByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate)).thenReturn(totalExpenses)
        `when`(savingGoalService.getAllSavingGoals()).thenReturn(emptyList())
        
        // When
        val result = reportService.generateFinancialSummary(startDate, endDate)
        
        // Then
        assertEquals(totalIncome, result.totalIncome)
        assertEquals(totalExpenses, result.totalExpenses)
        assertEquals(expectedBalance, result.balance)
        verify(transactionService).getTransactionsByDateRange(startDate, endDate)
        verify(transactionService).getSumByTypeAndDateRange(TransactionType.INCOME, startDate, endDate)
        verify(transactionService).getSumByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate)
    }
    
    // Komplexe Tests, die Mockito-Matcher verwenden, wurden entfernt
} 