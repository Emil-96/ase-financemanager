package com.emil.financemanager.repository

import com.emil.financemanager.model.SavingGoal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SavingGoalRepository : JpaRepository<SavingGoal, Long> {
    fun findByName(name: String): SavingGoal?
    fun findByTargetDateBefore(date: LocalDate): List<SavingGoal>
}

@Repository
interface SavingContributionRepository : JpaRepository<com.emil.financemanager.model.SavingContribution, Long> {
    fun findBySavingGoalId(savingGoalId: Long): List<com.emil.financemanager.model.SavingContribution>
} 