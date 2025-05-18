package com.emil.financemanager.controller

import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.model.TransactionType
import com.emil.financemanager.service.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime

@Controller
class HomeController(
    private val transactionService: TransactionService,
    private val categoryService: CategoryService,
    private val budgetService: BudgetService,
    private val savingGoalService: SavingGoalService,
    private val reportService: ReportService,
    private val notificationService: NotificationService
) {
    
    @GetMapping("/")
    fun home(model: Model): String {
        val now = LocalDateTime.now()
        val startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        
        // Get data for dashboard
        val currentMonthSummary = reportService.generateCurrentMonthSummary()
        val currentBudgets = budgetService.getCurrentBudgets()
        val savingGoals = savingGoalService.getAllSavingGoals()
            .map { it to savingGoalService.calculateGoalProgress(it.id) }
        val unreadNotifications = notificationService.getUnread()
        
        // Add data to model
        model.addAttribute("summary", currentMonthSummary)
        model.addAttribute("budgets", currentBudgets)
        model.addAttribute("savingGoals", savingGoals)
        model.addAttribute("notifications", unreadNotifications)
        
        // Check for budget thresholds and saving goals
        notificationService.checkBudgetThresholds()
        
        return "home"
    }
    
    @GetMapping("/transactions")
    fun transactions(model: Model): String {
        model.addAttribute("transactions", transactionService.getAllTransactions())
        model.addAttribute("categories", categoryService.getAllCategories())
        model.addAttribute("transactionTypes", TransactionType.values())
        return "transactions"
    }
    
    @GetMapping("/budgets")
    fun budgets(model: Model): String {
        model.addAttribute("budgets", budgetService.getAllBudgets())
        model.addAttribute("categories", categoryService.getCategoriesByType(CategoryType.EXPENSE))
        return "budgets"
    }
    
    @GetMapping("/savings")
    fun savings(model: Model): String {
        model.addAttribute("savingGoals", savingGoalService.getAllSavingGoals())
        return "savings"
    }
    
    @GetMapping("/reports")
    fun reports(model: Model): String {
        model.addAttribute("currentMonthSummary", reportService.generateCurrentMonthSummary())
        model.addAttribute("lastMonthSummary", reportService.generateLastMonthSummary())
        model.addAttribute("last30DaysSummary", reportService.generateLastDaysSummary(30))
        return "reports"
    }
    
    @GetMapping("/categories")
    fun categories(model: Model): String {
        model.addAttribute("categories", categoryService.getAllCategories())
        model.addAttribute("categoryTypes", CategoryType.values())
        return "categories"
    }
    
    @GetMapping("/import")
    fun importPage(model: Model): String {
        model.addAttribute("categories", categoryService.getAllCategories())
        return "import"
    }
} 