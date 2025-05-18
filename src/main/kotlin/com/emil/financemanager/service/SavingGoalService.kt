package com.emil.financemanager.service

import com.emil.financemanager.model.SavingContribution
import com.emil.financemanager.model.SavingGoal
import com.emil.financemanager.repository.SavingContributionRepository
import com.emil.financemanager.repository.SavingGoalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class SavingGoalService(
    private val savingGoalRepository: SavingGoalRepository,
    private val savingContributionRepository: SavingContributionRepository
) {
    
    fun getAllSavingGoals(): List<SavingGoal> = savingGoalRepository.findAll()
    
    fun getSavingGoalById(id: Long): SavingGoal = savingGoalRepository.findById(id)
        .orElseThrow { NoSuchElementException("Saving goal not found with id: $id") }
    
    fun getSavingGoalByName(name: String): SavingGoal? = savingGoalRepository.findByName(name)
    
    fun getUpcomingSavingGoals(): List<SavingGoal> {
        val today = LocalDate.now()
        return savingGoalRepository.findByTargetDateBefore(today.plusMonths(3))
    }
    
    @Transactional
    fun createSavingGoal(savingGoal: SavingGoal): SavingGoal {
        if (savingGoalRepository.findByName(savingGoal.name) != null) {
            throw IllegalArgumentException("Saving goal with name '${savingGoal.name}' already exists")
        }
        return savingGoalRepository.save(savingGoal)
    }
    
    @Transactional
    fun updateSavingGoal(id: Long, savingGoal: SavingGoal): SavingGoal {
        val existingGoal = getSavingGoalById(id)
        
        val updated = existingGoal.copy(
            name = savingGoal.name,
            description = savingGoal.description,
            targetAmount = savingGoal.targetAmount,
            currentAmount = savingGoal.currentAmount,
            targetDate = savingGoal.targetDate
        )
        
        return savingGoalRepository.save(updated)
    }
    
    @Transactional
    fun deleteSavingGoal(id: Long) {
        val savingGoal = getSavingGoalById(id)
        savingGoalRepository.delete(savingGoal)
    }
    
    @Transactional
    fun addContribution(savingGoalId: Long, contribution: SavingContribution): SavingContribution {
        val savingGoal = getSavingGoalById(savingGoalId)
        
        // Create and save the contribution
        val newContribution = contribution.copy(savingGoal = savingGoal)
        val savedContribution = savingContributionRepository.save(newContribution)
        
        // Update the goal's current amount
        val updatedGoal = savingGoal.copy(
            currentAmount = savingGoal.currentAmount.add(contribution.amount)
        )
        savingGoalRepository.save(updatedGoal)
        
        return savedContribution
    }
    
    fun getContributionsForGoal(savingGoalId: Long): List<SavingContribution> =
        savingContributionRepository.findBySavingGoalId(savingGoalId)
    
    /**
     * Calculates the progress percentage toward the saving goal
     * @return percentage of goal achieved (0-100)
     */
    fun calculateGoalProgress(savingGoalId: Long): Int {
        val goal = getSavingGoalById(savingGoalId)
        
        if (goal.targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0
        }
        
        return ((goal.currentAmount.divide(goal.targetAmount, 2, RoundingMode.HALF_UP)) 
            * BigDecimal(100)).toInt()
    }
    
    /**
     * Calculates the recommended monthly contribution to reach the goal
     */
    fun calculateRecommendedMonthlyContribution(savingGoalId: Long): BigDecimal {
        val goal = getSavingGoalById(savingGoalId)
        val today = LocalDate.now()
        
        // If target date is in the past, recommend the full remaining amount
        if (goal.targetDate.isBefore(today)) {
            return goal.targetAmount.subtract(goal.currentAmount)
        }
        
        // Calculate months remaining
        val monthsRemaining = ChronoUnit.MONTHS.between(today, goal.targetDate).toInt()
        
        if (monthsRemaining <= 0) {
            return goal.targetAmount.subtract(goal.currentAmount)
        }
        
        // Calculate amount still needed
        val amountNeeded = goal.targetAmount.subtract(goal.currentAmount)
        
        // Return monthly contribution rounded to 2 decimal places
        return amountNeeded.divide(BigDecimal(monthsRemaining), 2, RoundingMode.CEILING)
    }
} 