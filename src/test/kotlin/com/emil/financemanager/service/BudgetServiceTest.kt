package com.emil.financemanager.service

import com.emil.financemanager.model.Budget
import com.emil.financemanager.model.Category
import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionType
import com.emil.financemanager.repository.BudgetRepository
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
class BudgetServiceTest {
    
    @Mock
    private lateinit var budgetRepository: BudgetRepository
    
    @Mock
    private lateinit var categoryService: CategoryService
    
    @InjectMocks
    private lateinit var budgetService: BudgetService
    
    private lateinit var testCategory: Category
    private lateinit var testBudget: Budget
    
    @BeforeEach
    fun setup() {
        testCategory = Category(
            id = 1L,
            name = "Test Category",
            type = CategoryType.EXPENSE
        )
        
        testBudget = Budget(
            id = 1L,
            name = "Test Budget",
            category = testCategory,
            amount = BigDecimal("1000.00"),
            startDate = LocalDate.now().withDayOfMonth(1),
            endDate = LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1),
            thresholdPercentage = 80
        )
    }
    
    @Test
    fun `should return all budgets`() {
        // Given
        val budgets = listOf(testBudget)
        `when`(budgetRepository.findAll()).thenReturn(budgets)
        
        // When
        val result = budgetService.getAllBudgets()
        
        // Then
        assertEquals(budgets, result)
        verify(budgetRepository).findAll()
    }
    
    @Test
    fun `should return budget by id`() {
        // Given
        `when`(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget))
        
        // When
        val result = budgetService.getBudgetById(1L)
        
        // Then
        assertEquals(testBudget, result)
        verify(budgetRepository).findById(1L)
    }
    
    @Test
    fun `should throw exception when budget not found`() {
        // Given
        `when`(budgetRepository.findById(99L)).thenReturn(Optional.empty())
        
        // When & Then
        val exception = assertThrows(NoSuchElementException::class.java) {
            budgetService.getBudgetById(99L)
        }
        
        assertTrue(exception.message!!.contains("not found"))
        verify(budgetRepository).findById(99L)
    }
    
    @Test
    fun `should return current budgets`() {
        // Given
        val today = LocalDate.now()
        val budgets = listOf(testBudget)
        
        `when`(budgetRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today))
            .thenReturn(budgets)
        
        // When
        val result = budgetService.getCurrentBudgets()
        
        // Then
        assertEquals(budgets, result)
        verify(budgetRepository).findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
    }
    
    @Test
    fun `should return budgets by category`() {
        // Given
        val budgets = listOf(testBudget)
        `when`(categoryService.getCategoryById(1L)).thenReturn(testCategory)
        `when`(budgetRepository.findByCategory(testCategory)).thenReturn(budgets)
        
        // When
        val result = budgetService.getBudgetsByCategory(1L)
        
        // Then
        assertEquals(budgets, result)
        verify(categoryService).getCategoryById(1L)
        verify(budgetRepository).findByCategory(testCategory)
    }
    
    @Test
    fun `should create budget`() {
        // Given
        val newBudget = testBudget.copy(id = 0L)
        `when`(budgetRepository.save(newBudget)).thenReturn(testBudget)
        
        // When
        val result = budgetService.createBudget(newBudget)
        
        // Then
        assertEquals(testBudget, result)
        verify(budgetRepository).save(newBudget)
    }
    
    @Test
    fun `should update budget`() {
        // Given
        val updatedBudget = testBudget.copy(
            name = "Updated Budget",
            amount = BigDecimal("1500.00")
        )
        
        `when`(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget))
        `when`(budgetRepository.save(any())).thenReturn(updatedBudget)
        
        // When
        val result = budgetService.updateBudget(1L, updatedBudget)
        
        // Then
        assertEquals(updatedBudget, result)
        verify(budgetRepository).findById(1L)
        verify(budgetRepository).save(any())
    }
    
    @Test
    fun `should delete budget`() {
        // Given
        `when`(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget))
        
        // When
        budgetService.deleteBudget(1L)
        
        // Then
        verify(budgetRepository).findById(1L)
        verify(budgetRepository).delete(testBudget)
    }
    
    @Test
    fun `should update budget with transaction`() {
        // Given
        val transaction = MoneyTransaction(
            id = 1L,
            amount = BigDecimal("100.00"),
            description = "Test Transaction",
            category = testCategory,
            type = TransactionType.EXPENSE,
            timestamp = LocalDateTime.now()
        )
        
        `when`(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget))
        `when`(budgetRepository.save(any())).thenReturn(testBudget)
        
        // When
        budgetService.updateBudgetWithTransaction(1L, transaction)
        
        // Then
        verify(budgetRepository).findById(1L)
        verify(budgetRepository).save(any())
    }
} 