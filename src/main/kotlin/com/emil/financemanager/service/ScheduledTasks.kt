package com.emil.financemanager.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory

@Component
class ScheduledTasks(
    private val notificationService: NotificationService
) {
    private val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)
    
    /**
     * Check budget thresholds daily
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    fun checkBudgetThresholds() {
        logger.info("Running scheduled task: check budget thresholds")
        notificationService.checkBudgetThresholds()
    }
    
    /**
     * Check saving goal contributions weekly
     */
    @Scheduled(cron = "0 0 12 ? * SUN") // Run at noon on Sunday
    fun checkSavingGoals() {
        logger.info("Running scheduled task: check saving goal contributions")
        notificationService.checkSavingGoalContributions()
    }
} 