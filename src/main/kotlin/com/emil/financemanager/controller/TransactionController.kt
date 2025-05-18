package com.emil.financemanager.controller

import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionDTO
import com.emil.financemanager.model.TransactionType
import com.emil.financemanager.service.BudgetService
import com.emil.financemanager.service.CategoryService
import com.emil.financemanager.service.TransactionService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val categoryService: CategoryService,
    private val budgetService: BudgetService
) {

    @GetMapping
    fun getAllTransactions(): ResponseEntity<List<MoneyTransaction>> =
        ResponseEntity.ok(transactionService.getAllTransactions())
    
    @GetMapping("/{id}")
    fun getTransactionById(@PathVariable id: Long): ResponseEntity<MoneyTransaction> =
        ResponseEntity.ok(transactionService.getTransactionById(id))
    
    @GetMapping("/type/{type}")
    fun getTransactionsByType(@PathVariable type: TransactionType): ResponseEntity<List<MoneyTransaction>> =
        ResponseEntity.ok(transactionService.getTransactionsByType(type))
    
    @GetMapping("/category/{categoryId}")
    fun getTransactionsByCategory(@PathVariable categoryId: Long): ResponseEntity<List<MoneyTransaction>> =
        ResponseEntity.ok(transactionService.getTransactionsByCategory(categoryId))
    
    @GetMapping("/date-range")
    fun getTransactionsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: LocalDateTime
    ): ResponseEntity<List<MoneyTransaction>> =
        ResponseEntity.ok(transactionService.getTransactionsByDateRange(start, end))
    
    @GetMapping("/sum/type/{type}")
    fun getSumByType(@PathVariable type: TransactionType): ResponseEntity<BigDecimal> =
        ResponseEntity.ok(transactionService.getSumByType(type))
    
    @GetMapping("/sum/type/{type}/date-range")
    fun getSumByTypeAndDateRange(
        @PathVariable type: TransactionType,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: LocalDateTime
    ): ResponseEntity<BigDecimal> =
        ResponseEntity.ok(transactionService.getSumByTypeAndDateRange(type, start, end))
    
    @GetMapping("/sum/category/{categoryId}/date-range")
    fun getSumByCategoryAndDateRange(
        @PathVariable categoryId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: LocalDateTime
    ): ResponseEntity<BigDecimal> =
        ResponseEntity.ok(transactionService.getSumByCategoryAndDateRange(categoryId, start, end))
    
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createTransactionJson(@RequestBody transaction: MoneyTransaction): ResponseEntity<MoneyTransaction> =
        ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transaction))
    
    @PostMapping(consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun createTransactionForm(@ModelAttribute transactionDTO: TransactionDTO): ResponseEntity<MoneyTransaction> {
        val transaction = transactionDTO.toEntity(categoryService, budgetService)
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transaction))
    }
    
    @PutMapping("/{id}")
    fun updateTransaction(
        @PathVariable id: Long,
        @RequestBody transaction: MoneyTransaction
    ): ResponseEntity<MoneyTransaction> =
        ResponseEntity.ok(transactionService.updateTransaction(id, transaction))
    
    @DeleteMapping("/{id}")
    fun deleteTransaction(@PathVariable id: Long): ResponseEntity<Unit> {
        transactionService.deleteTransaction(id)
        return ResponseEntity.noContent().build()
    }
} 