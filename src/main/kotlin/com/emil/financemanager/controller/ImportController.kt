package com.emil.financemanager.controller

import com.emil.financemanager.service.ImportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/import")
class ImportController(private val importService: ImportService) {
    
    @PostMapping("/transactions")
    fun importTransactions(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("defaultCategoryId", required = false) defaultCategoryId: Long?
    ): ResponseEntity<ImportService.ImportResult> {
        val result = importService.importTransactionsFromCsv(file, defaultCategoryId)
        return ResponseEntity.ok(result)
    }
} 