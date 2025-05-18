package com.emil.financemanager.service

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionType
import com.emil.financemanager.repository.TransactionRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {
    
    @Mock
    private lateinit var transactionRepository: TransactionRepository
    
    @Mock
    private lateinit var categoryService: CategoryService
    
    @Mock
    private lateinit var budgetService: BudgetService
    
    @InjectMocks
    private lateinit var transactionService: TransactionService
    
    private lateinit var testCategory: Category
    private lateinit var testTransaction: MoneyTransaction
    
    @BeforeEach
    fun setup() {
        testCategory = Category(
            id = 1L,
            name = "Test Category",
            type = CategoryType.EXPENSE
        )
        
        testTransaction = MoneyTransaction(
            id = 1L,
            amount = BigDecimal("100.00"),
            description = "Test Transaction",
            timestamp = LocalDateTime.now(),
            category = testCategory,
            type = TransactionType.EXPENSE
        )
    }
    
    @Test
    fun `should return all transactions`() {
        // Given
        val transactions = listOf(testTransaction)
        `when`(transactionRepository.findAll()).thenReturn(transactions)
        
        // When
        val result = transactionService.getAllTransactions()
        
        // Then
        assertEquals(transactions, result)
        verify(transactionRepository).findAll()
    }
    
    @Test
    fun `should return transaction by id`() {
        // Given
        `when`(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction))
        
        // When
        val result = transactionService.getTransactionById(1L)
        
        // Then
        assertEquals(testTransaction, result)
        verify(transactionRepository).findById(1L)
    }
    
    @Test
    fun `should throw exception when transaction not found`() {
        // Given
        `when`(transactionRepository.findById(99L)).thenReturn(Optional.empty())
        
        // When & Then
        val exception = assertThrows(NoSuchElementException::class.java) {
            transactionService.getTransactionById(99L)
        }
        
        assertTrue(exception.message!!.contains("not found"))
        verify(transactionRepository).findById(99L)
    }
    
    @Test
    fun `should return transactions by type`() {
        // Given
        val transactions = listOf(testTransaction)
        `when`(transactionRepository.findByType(TransactionType.EXPENSE)).thenReturn(transactions)
        
        // When
        val result = transactionService.getTransactionsByType(TransactionType.EXPENSE)
        
        // Then
        assertEquals(transactions, result)
        verify(transactionRepository).findByType(TransactionType.EXPENSE)
    }
    
    @Test
    fun `should return transactions by category`() {
        // Given
        val transactions = listOf(testTransaction)
        `when`(categoryService.getCategoryById(1L)).thenReturn(testCategory)
        `when`(transactionRepository.findByCategory(testCategory)).thenReturn(transactions)
        
        // When
        val result = transactionService.getTransactionsByCategory(1L)
        
        // Then
        assertEquals(transactions, result)
        verify(categoryService).getCategoryById(1L)
        verify(transactionRepository).findByCategory(testCategory)
    }
    
    @Test
    fun `should return transactions by date range`() {
        // Given
        val start = LocalDateTime.now().minusDays(7)
        val end = LocalDateTime.now()
        val transactions = listOf(testTransaction)
        
        `when`(transactionRepository.findByTimestampBetween(start, end)).thenReturn(transactions)
        
        // When
        val result = transactionService.getTransactionsByDateRange(start, end)
        
        // Then
        assertEquals(transactions, result)
        verify(transactionRepository).findByTimestampBetween(start, end)
    }
    
    @Test
    fun `should create transaction`() {
        // Given
        val newTransaction = testTransaction.copy(id = 0L)
        `when`(transactionRepository.save(newTransaction)).thenReturn(testTransaction)
        
        // When
        val result = transactionService.createTransaction(newTransaction)
        
        // Then
        assertEquals(testTransaction, result)
        verify(transactionRepository).save(newTransaction)
    }
    
    @Test
    fun `should update transaction`() {
        // Given
        val updatedTransaction = testTransaction.copy(
            amount = BigDecimal("200.00"),
            description = "Updated Transaction"
        )
        
        `when`(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction))
        `when`(transactionRepository.save(any())).thenReturn(updatedTransaction)
        
        // When
        val result = transactionService.updateTransaction(1L, updatedTransaction)
        
        // Then
        assertEquals(updatedTransaction, result)
        verify(transactionRepository).findById(1L)
        verify(transactionRepository).save(any())
    }
    
    @Test
    fun `should delete transaction`() {
        // Given
        `when`(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction))
        
        // When
        transactionService.deleteTransaction(1L)
        
        // Then
        verify(transactionRepository).findById(1L)
        verify(transactionRepository).delete(testTransaction)
    }
} 