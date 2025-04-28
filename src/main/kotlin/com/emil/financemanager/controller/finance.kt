package com.emil.financemanager.controller

import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.service.FinanceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/finance")
class FinanceController(
    private val financeService: FinanceService
){
    @PostMapping("/transaction")
    fun addTransaction(@RequestBody transaction: MoneyTransaction): MoneyTransaction {
        return financeService.addTransaction(transaction)
    }

    @GetMapping("/balance")
    fun getBalance(): BigDecimal {
        return financeService.getBalance()
    }

    @GetMapping("/transactions")
    fun getAllTransactions(): List<MoneyTransaction> {
        return financeService.listTransactions()
    }
}