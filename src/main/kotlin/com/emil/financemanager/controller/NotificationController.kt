package com.emil.financemanager.controller

import com.emil.financemanager.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(private val notificationService: NotificationService) {
    
    @GetMapping
    fun getAllNotifications(): ResponseEntity<List<NotificationService.Notification>> =
        ResponseEntity.ok(notificationService.getAll())
    
    @GetMapping("/unread")
    fun getUnreadNotifications(): ResponseEntity<List<NotificationService.Notification>> =
        ResponseEntity.ok(notificationService.getUnread())
    
    @PutMapping("/{id}/read")
    fun markAsRead(@PathVariable id: String): ResponseEntity<Unit> {
        notificationService.markAsRead(id)
        return ResponseEntity.ok().build()
    }
    
    @PutMapping("/mark-all-read")
    fun markAllAsRead(): ResponseEntity<Unit> {
        notificationService.markAllAsRead()
        return ResponseEntity.ok().build()
    }
    
    @PostMapping("/check-budget-thresholds")
    fun checkBudgetThresholds(): ResponseEntity<Unit> {
        notificationService.checkBudgetThresholds()
        return ResponseEntity.ok().build()
    }
    
    @PostMapping("/check-saving-goals")
    fun checkSavingGoalContributions(): ResponseEntity<Unit> {
        notificationService.checkSavingGoalContributions()
        return ResponseEntity.ok().build()
    }
} 