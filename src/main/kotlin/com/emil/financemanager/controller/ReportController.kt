package com.emil.financemanager.controller

import com.emil.financemanager.service.ReportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/reports")
class ReportController(private val reportService: ReportService) {

    @GetMapping("/summary")
    fun getFinancialSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<ReportService.FinancialSummary> =
        ResponseEntity.ok(reportService.generateFinancialSummary(startDate, endDate))

    @GetMapping("/summary/current-month")
    fun getCurrentMonthSummary(): ResponseEntity<ReportService.FinancialSummary> =
        ResponseEntity.ok(reportService.generateCurrentMonthSummary())
    
    @GetMapping("/summary/last-month")
    fun getLastMonthSummary(): ResponseEntity<ReportService.FinancialSummary> =
        ResponseEntity.ok(reportService.generateLastMonthSummary())
    
    @GetMapping("/summary/last-days/{days}")
    fun getLastDaysSummary(@PathVariable days: Int): ResponseEntity<ReportService.FinancialSummary> =
        ResponseEntity.ok(reportService.generateLastDaysSummary(days))
} 