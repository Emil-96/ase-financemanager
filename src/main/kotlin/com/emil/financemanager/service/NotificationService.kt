package com.emil.financemanager.service

import com.emil.financemanager.model.Budget
import com.emil.financemanager.model.SavingGoal
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class NotificationService(
    private val budgetService: BudgetService,
    private val savingGoalService: SavingGoalService
) {
    
    data class Notification(
        val id: String = java.util.UUID.randomUUID().toString(),
        val title: String,
        val message: String,
        val type: NotificationType,
        val date: LocalDate = LocalDate.now(),
        val isRead: Boolean = false,
        val relatedId: Long? = null
    )
    
    enum class NotificationType {
        BUDGET_THRESHOLD, SAVING_GOAL_REMINDER, GENERAL
    }
    
    private val notifications = mutableListOf<Notification>()
    
    fun getAll(): List<Notification> = notifications.toList()
    
    fun getUnread(): List<Notification> = notifications.filter { !it.isRead }
    
    fun markAsRead(id: String) {
        val notification = notifications.find { it.id == id }
        notification?.let {
            notifications.remove(it)
            notifications.add(it.copy(isRead = true))
        }
    }
    
    fun markAllAsRead() {
        val readNotifications = notifications.map { it.copy(isRead = true) }
        notifications.clear()
        notifications.addAll(readNotifications)
    }
    
    fun addNotification(notification: Notification) {
        notifications.add(notification)
    }
    
    /**
     * Check all budgets and create notifications for those exceeding threshold
     */
    fun checkBudgetThresholds() {
        val currentBudgets = budgetService.getCurrentBudgets()
        
        for (budget in currentBudgets) {
            if (budgetService.isBudgetThresholdExceeded(budget.id)) {
                val spendingPercentage = budgetService.calculateBudgetSpendingPercentage(budget.id)
                
                val notification = Notification(
                    title = "Budget threshold exceeded",
                    message = "Your budget '${budget.name}' has reached ${spendingPercentage}% of the allocated amount.",
                    type = NotificationType.BUDGET_THRESHOLD,
                    relatedId = budget.id
                )
                
                addNotification(notification)
            }
        }
    }
    
    /**
     * Check saving goals and create reminders
     */
    fun checkSavingGoalContributions() {
        val savingGoals = savingGoalService.getAllSavingGoals()
        val today = LocalDate.now()
        
        for (goal in savingGoals) {
            // Skip completed goals
            if (savingGoalService.calculateGoalProgress(goal.id) >= 100) {
                continue
            }
            
            // Check for goals that need contributions
            val progress = savingGoalService.calculateGoalProgress(goal.id)
            val monthlyAmount = savingGoalService.calculateRecommendedMonthlyContribution(goal.id)
            
            // If target date is approaching and progress is low, send a notification
            if (goal.targetDate.isBefore(today.plusMonths(3)) && progress < 80) {
                val notification = Notification(
                    title = "Saving goal reminder",
                    message = "Your saving goal '${goal.name}' is at $progress%. " +
                            "Consider contributing $monthlyAmount per month to reach your target.",
                    type = NotificationType.SAVING_GOAL_REMINDER,
                    relatedId = goal.id
                )
                
                addNotification(notification)
            }
        }
    }
} 