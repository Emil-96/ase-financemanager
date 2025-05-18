package com.emil.financemanager.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class SavingGoal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val name: String,
    
    val description: String? = null,
    
    val targetAmount: BigDecimal,
    
    val currentAmount: BigDecimal = BigDecimal.ZERO,
    
    val startDate: LocalDate = LocalDate.now(),
    
    val targetDate: LocalDate,
    
    @OneToMany(mappedBy = "savingGoal", cascade = [CascadeType.ALL])
    val contributions: MutableSet<SavingContribution> = mutableSetOf()
)

@Entity
data class SavingContribution(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val amount: BigDecimal,
    
    val date: LocalDate = LocalDate.now(),
    
    val description: String? = null,
    
    @ManyToOne
    val savingGoal: SavingGoal
) 