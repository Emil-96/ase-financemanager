package com.emil.financemanager.repository

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface TransactionRepository : JpaRepository<MoneyTransaction, Long> {
    fun findByType(type: TransactionType): List<MoneyTransaction>
    fun findByCategory(category: Category): List<MoneyTransaction>
    fun findByTimestampBetween(start: LocalDateTime, end: LocalDateTime): List<MoneyTransaction>
    
    @Query("SELECT SUM(t.amount) FROM MoneyTransaction t WHERE t.type = :type")
    fun sumByType(type: TransactionType): BigDecimal?
    
    @Query("SELECT SUM(t.amount) FROM MoneyTransaction t WHERE t.type = :type AND t.timestamp BETWEEN :start AND :end")
    fun sumByTypeAndTimestampBetween(type: TransactionType, start: LocalDateTime, end: LocalDateTime): BigDecimal?
    
    @Query("SELECT SUM(t.amount) FROM MoneyTransaction t WHERE t.category = :category AND t.timestamp BETWEEN :start AND :end")
    fun sumByCategoryAndTimestampBetween(category: Category, start: LocalDateTime, end: LocalDateTime): BigDecimal?
} 