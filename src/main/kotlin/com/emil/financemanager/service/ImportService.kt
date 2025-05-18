package com.emil.financemanager.service

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.model.MoneyTransaction
import com.emil.financemanager.model.TransactionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class ImportService(
    private val transactionService: TransactionService,
    private val categoryService: CategoryService
) {
    
    // Supported date formats
    private val dateFormats = listOf(
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
    )
    
    data class ImportResult(
        val totalProcessed: Int,
        val successCount: Int,
        val failures: List<String>,
        val duplicates: Int
    )
    
    /**
     * Import transactions from a CSV file
     * Expected format: date,amount,description,category,type
     */
    @Transactional
    fun importTransactionsFromCsv(file: MultipartFile, defaultCategoryId: Long? = null): ImportResult {
        val reader = BufferedReader(InputStreamReader(file.inputStream))
        val lines = reader.readLines()
        
        val existingTransactions = transactionService.getAllTransactions()
        
        var totalProcessed = 0
        val successfulImports = mutableListOf<MoneyTransaction>()
        val failures = mutableListOf<String>()
        var duplicateCount = 0
        
        // Skip header row if present
        val dataLines = if (lines.firstOrNull()?.contains("date", ignoreCase = true) == true) {
            lines.drop(1)
        } else {
            lines
        }
        
        for (line in dataLines) {
            if (line.isBlank()) continue
            
            totalProcessed++
            
            try {
                val columns = line.split(",")
                
                if (columns.size < 3) {
                    failures.add("Line has insufficient columns: $line")
                    continue
                }
                
                // Parse date
                val dateStr = columns[0].trim()
                val date = parseDate(dateStr)
                
                // Parse amount
                val amount = BigDecimal(columns[1].trim())
                
                // Get description
                val description = columns[2].trim()
                
                // Determine category
                val categoryName = if (columns.size > 3) columns[3].trim() else "Uncategorized"
                val category = findOrCreateCategory(categoryName, defaultCategoryId)
                
                // Determine transaction type
                val typeStr = if (columns.size > 4) columns[4].trim() else "EXPENSE"
                val type = when (typeStr.uppercase()) {
                    "INCOME" -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                
                // Create transaction object
                val transaction = MoneyTransaction(
                    amount = amount.abs(),
                    description = description,
                    timestamp = date,
                    category = category,
                    type = type
                )
                
                // Check for duplicates
                val isDuplicate = existingTransactions.any { existing ->
                    existing.amount == transaction.amount &&
                    existing.description == transaction.description &&
                    existing.timestamp == transaction.timestamp
                }
                
                if (isDuplicate) {
                    duplicateCount++
                    continue
                }
                
                // Save transaction
                val savedTransaction = transactionService.createTransaction(transaction)
                successfulImports.add(savedTransaction)
                
            } catch (e: Exception) {
                failures.add("Error processing line: $line. ${e.message}")
            }
        }
        
        return ImportResult(
            totalProcessed = totalProcessed,
            successCount = successfulImports.size,
            failures = failures,
            duplicates = duplicateCount
        )
    }
    
    private fun parseDate(dateStr: String): LocalDateTime {
        for (formatter in dateFormats) {
            try {
                return LocalDateTime.parse(dateStr, formatter)
            } catch (e: Exception) {
                // Try next format
            }
        }
        
        // If none of the formats work, default to current time
        return LocalDateTime.now()
    }
    
    private fun findOrCreateCategory(categoryName: String, defaultCategoryId: Long?): Category {
        // Try to find existing category
        val existingCategory = categoryService.getAllCategories()
            .find { it.name.equals(categoryName, ignoreCase = true) }
        
        if (existingCategory != null) {
            return existingCategory
        }
        
        // Use default category if provided
        if (defaultCategoryId != null) {
            return categoryService.getCategoryById(defaultCategoryId)
        }
        
        // Create a new category
        val newCategory = Category(
            name = categoryName,
            type = if (categoryName.contains("income", ignoreCase = true)) {
                CategoryType.INCOME
            } else {
                CategoryType.EXPENSE
            }
        )
        
        return categoryService.createCategory(newCategory)
    }
} 