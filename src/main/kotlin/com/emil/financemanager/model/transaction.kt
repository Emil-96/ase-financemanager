package com.emil.financemanager.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
data class MoneyTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val amount: BigDecimal,
    val description: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @ManyToOne
    val category: Category,
    
    val type: TransactionType,
    
    @ManyToOne
    val budget: Budget? = null
)

enum class TransactionType {
    INCOME, EXPENSE
}