package com.emil.financemanager.controller

import com.emil.financemanager.model.Budget
import com.emil.financemanager.service.BudgetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/budgets")
class BudgetController(private val budgetService: BudgetService) {

    @GetMapping
    fun getAllBudgets(): ResponseEntity<List<Budget>> =
        ResponseEntity.ok(budgetService.getAllBudgets())
    
    @GetMapping("/{id}")
    fun getBudgetById(@PathVariable id: Long): ResponseEntity<Budget> =
        ResponseEntity.ok(budgetService.getBudgetById(id))
    
    @GetMapping("/current")
    fun getCurrentBudgets(): ResponseEntity<List<Budget>> =
        ResponseEntity.ok(budgetService.getCurrentBudgets())
    
    @GetMapping("/category/{categoryId}")
    fun getBudgetsByCategory(@PathVariable categoryId: Long): ResponseEntity<List<Budget>> =
        ResponseEntity.ok(budgetService.getBudgetsByCategory(categoryId))
    
    @GetMapping("/{id}/spending-percentage")
    fun getBudgetSpendingPercentage(@PathVariable id: Long): ResponseEntity<Int> =
        ResponseEntity.ok(budgetService.calculateBudgetSpendingPercentage(id))
    
    @GetMapping("/{id}/threshold-exceeded")
    fun isBudgetThresholdExceeded(@PathVariable id: Long): ResponseEntity<Boolean> =
        ResponseEntity.ok(budgetService.isBudgetThresholdExceeded(id))
    
    @PostMapping
    fun createBudget(@RequestBody budget: Budget): ResponseEntity<Budget> =
        ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(budget))
    
    @PutMapping("/{id}")
    fun updateBudget(
        @PathVariable id: Long,
        @RequestBody budget: Budget
    ): ResponseEntity<Budget> =
        ResponseEntity.ok(budgetService.updateBudget(id, budget))
    
    @DeleteMapping("/{id}")
    fun deleteBudget(@PathVariable id: Long): ResponseEntity<Unit> {
        budgetService.deleteBudget(id)
        return ResponseEntity.noContent().build()
    }
} 