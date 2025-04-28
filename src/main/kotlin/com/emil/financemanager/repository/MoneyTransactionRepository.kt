package com.emil.financemanager.repository

import com.emil.financemanager.model.MoneyTransaction
import org.springframework.data.jpa.repository.JpaRepository

interface MoneyTransactionRepository : JpaRepository<MoneyTransaction, Long>