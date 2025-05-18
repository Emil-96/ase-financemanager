package com.emil.financemanager.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class Budget(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val name: String,
    
    @ManyToOne
    val category: Category,
    
    val amount: BigDecimal,
    
    val startDate: LocalDate,
    
    val endDate: LocalDate,
    
    @OneToMany(mappedBy = "budget", cascade = [CascadeType.ALL])
    val transactions: MutableSet<MoneyTransaction> = mutableSetOf(),
    
    @Column(name = "threshold_percentage")
    val thresholdPercentage: Int = 80 // Default notification threshold at 80%
) 