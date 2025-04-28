package com.emil.financemanager.service

import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.repository.MoneyTransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class FinanceService(
    private val transactionRepository: MoneyTransactionRepository
){
    fun addTransaction(transaction: MoneyTransaction): MoneyTransaction {
        return transactionRepository.save(transaction)
    }

    fun getBalance(): BigDecimal {
        val transactions = transactionRepository.findAll()
        return transactions.fold(BigDecimal.ZERO){ acc, tx -> acc + tx.amount }
    }

    fun listTransactions(): List<MoneyTransaction> {
        return transactionRepository.findAll()
    }
}
