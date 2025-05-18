package com.emil.financemanager.repository

import com.emil.financemanager.model.Budget
import com.emil.financemanager.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface BudgetRepository : JpaRepository<Budget, Long> {
    fun findByCategory(category: Category): List<Budget>
    fun findByStartDateLessThanEqualAndEndDateGreaterThanEqual(date: LocalDate, sameDate: LocalDate): List<Budget>
    fun findByName(name: String): Budget?
} 