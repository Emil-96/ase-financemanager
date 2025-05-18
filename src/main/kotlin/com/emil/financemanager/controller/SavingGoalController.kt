package com.emil.financemanager.controller

import com.emil.financemanager.model.SavingContribution
import com.emil.financemanager.model.SavingGoal
import com.emil.financemanager.service.SavingGoalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/saving-goals")
class SavingGoalController(private val savingGoalService: SavingGoalService) {

    @GetMapping
    fun getAllSavingGoals(): ResponseEntity<List<SavingGoal>> =
        ResponseEntity.ok(savingGoalService.getAllSavingGoals())
    
    @GetMapping("/{id}")
    fun getSavingGoalById(@PathVariable id: Long): ResponseEntity<SavingGoal> =
        ResponseEntity.ok(savingGoalService.getSavingGoalById(id))
    
    @GetMapping("/upcoming")
    fun getUpcomingSavingGoals(): ResponseEntity<List<SavingGoal>> =
        ResponseEntity.ok(savingGoalService.getUpcomingSavingGoals())
    
    @GetMapping("/{id}/progress")
    fun getSavingGoalProgress(@PathVariable id: Long): ResponseEntity<Int> =
        ResponseEntity.ok(savingGoalService.calculateGoalProgress(id))
    
    @GetMapping("/{id}/monthly-contribution")
    fun getRecommendedMonthlyContribution(@PathVariable id: Long): ResponseEntity<BigDecimal> =
        ResponseEntity.ok(savingGoalService.calculateRecommendedMonthlyContribution(id))
    
    @GetMapping("/{id}/contributions")
    fun getContributionsForGoal(@PathVariable id: Long): ResponseEntity<List<SavingContribution>> =
        ResponseEntity.ok(savingGoalService.getContributionsForGoal(id))
    
    @PostMapping
    fun createSavingGoal(@RequestBody savingGoal: SavingGoal): ResponseEntity<SavingGoal> =
        ResponseEntity.status(HttpStatus.CREATED).body(savingGoalService.createSavingGoal(savingGoal))
    
    @PutMapping("/{id}")
    fun updateSavingGoal(
        @PathVariable id: Long,
        @RequestBody savingGoal: SavingGoal
    ): ResponseEntity<SavingGoal> =
        ResponseEntity.ok(savingGoalService.updateSavingGoal(id, savingGoal))
    
    @PostMapping("/{id}/contributions")
    fun addContribution(
        @PathVariable id: Long,
        @RequestBody contribution: SavingContribution
    ): ResponseEntity<SavingContribution> =
        ResponseEntity.status(HttpStatus.CREATED).body(savingGoalService.addContribution(id, contribution))
    
    @DeleteMapping("/{id}")
    fun deleteSavingGoal(@PathVariable id: Long): ResponseEntity<Unit> {
        savingGoalService.deleteSavingGoal(id)
        return ResponseEntity.noContent().build()
    }
} 