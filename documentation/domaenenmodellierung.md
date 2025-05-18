# Domänenmodellierung des Finance Managers

Dieses Dokument beschreibt die Domänenmodellierung des Finance Managers im Detail.

## Domänenmodell

Das Domänenmodell des Finance Managers basiert auf den folgenden Kernentitäten:

```
+----------------+       +----------------+       +----------------+
|                |       |                |       |                |
|MoneyTransaction+-------+ Category       +-------+ Budget         |
|                |       |                |       |                |
+----------------+       +----------------+       +----------------+
        |                        |                       |
        |                        |                       |
        v                        v                       v
+----------------+       +----------------+       +----------------+
|                |       |                |       |                |
| TransactionType|       | CategoryType   |       | SavingGoal     |
|                |       |                |       |                |
+----------------+       +----------------+       +----------------+
```

### Entities

#### MoneyTransaction

Eine `MoneyTransaction` repräsentiert einen finanziellen Vorgang (Einnahme oder Ausgabe):

```kotlin
@Entity
data class MoneyTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val amount: BigDecimal,
    val description: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @ManyToOne
    val category: Category,
    
    val type: TransactionType,
    
    @ManyToOne
    val budget: Budget? = null
)
```

#### Category

Eine `Category` klassifiziert Transaktionen:

```kotlin
@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val name: String,
    
    val type: CategoryType,
    
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL])
    val transactions: MutableSet<MoneyTransaction> = mutableSetOf()
)
```

#### Budget

Ein `Budget` definiert einen Ausgabenlimit für eine bestimmte Kategorie in einem Zeitraum:

```kotlin
@Entity
data class Budget(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val name: String,
    
    @ManyToOne
    val category: Category,
    
    val amount: BigDecimal,
    
    val startDate: LocalDate,
    
    val endDate: LocalDate,
    
    @OneToMany(mappedBy = "budget", cascade = [CascadeType.ALL])
    val transactions: MutableSet<MoneyTransaction> = mutableSetOf(),
    
    @Column(name = "threshold_percentage")
    val thresholdPercentage: Int = 80 // Default-Schwellenwert bei 80%
)
```

#### SavingGoal

Ein `SavingGoal` repräsentiert ein finanzielles Ziel:

```kotlin
@Entity
data class SavingGoal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val name: String,
    
    val targetAmount: BigDecimal,
    
    val currentAmount: BigDecimal = BigDecimal.ZERO,
    
    val targetDate: LocalDate,
    
    @ManyToOne
    val category: Category? = null
)
```

### Value Objects

#### TransactionType

```kotlin
enum class TransactionType {
    INCOME, EXPENSE
}
```

#### CategoryType

```kotlin
enum class CategoryType {
    INCOME, EXPENSE, SAVING
}
```

## Aggregate-Grenzen

In unserem DDD-Modell haben wir die folgenden Aggregate definiert:

### Transaction Aggregate

- Root: `MoneyTransaction`
- Enthaltene Entities: Keine
- Value Objects: `TransactionType`
- Verknüpfungen zu anderen Aggregates: `Category`, `Budget`

### Category Aggregate

- Root: `Category`
- Enthaltene Entities: Keine
- Value Objects: `CategoryType`
- Verknüpfungen: Referenziert von `MoneyTransaction`, `Budget`, `SavingGoal`

### Budget Aggregate

- Root: `Budget`
- Enthaltene Entities: Keine
- Value Objects: Keine
- Verknüpfungen: Referenziert von `MoneyTransaction`

### SavingGoal Aggregate

- Root: `SavingGoal`
- Enthaltene Entities: Keine
- Value Objects: Keine
- Verknüpfungen: `Category`

## Repository-Interfaces

Für jeden Aggregate-Root haben wir ein Repository-Interface definiert:

```kotlin
@Repository
interface TransactionRepository : JpaRepository<MoneyTransaction, Long> {
    fun findByType(type: TransactionType): List<MoneyTransaction>
    fun findByCategory(category: Category): List<MoneyTransaction>
    fun findByTimestampBetween(start: LocalDateTime, end: LocalDateTime): List<MoneyTransaction>
    
    @Query("SELECT SUM(t.amount) FROM MoneyTransaction t WHERE t.type = :type")
    fun sumByType(type: TransactionType): BigDecimal?
    
    @Query("SELECT SUM(t.amount) FROM MoneyTransaction t WHERE t.type = :type AND t.timestamp BETWEEN :start AND :end")
    fun sumByTypeAndTimestampBetween(type: TransactionType, start: LocalDateTime, end: LocalDateTime): BigDecimal?
    
    @Query("SELECT SUM(t.amount) FROM MoneyTransaction t WHERE t.category = :category AND t.timestamp BETWEEN :start AND :end")
    fun sumByCategoryAndTimestampBetween(category: Category, start: LocalDateTime, end: LocalDateTime): BigDecimal?
}

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByType(type: CategoryType): List<Category>
}

@Repository
interface BudgetRepository : JpaRepository<Budget, Long> {
    fun findByCategory(category: Category): List<Budget>
    fun findByCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        category: Category, 
        date1: LocalDate, 
        date2: LocalDate
    ): Optional<Budget>
}

@Repository
interface SavingGoalRepository : JpaRepository<SavingGoal, Long> {
    fun findByCategory(category: Category): List<SavingGoal>
}
```

## Domain Services

Die Domain Services implementieren die Geschäftslogik, die nicht natürlich zu einzelnen Entities gehört:

### TransactionService

```kotlin
@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryService: CategoryService,
    private val budgetService: BudgetService
) {
    // CRUD-Operationen
    
    @Transactional
    fun createTransaction(transaction: MoneyTransaction): MoneyTransaction {
        val savedTransaction = transactionRepository.save(transaction)
        
        // Wenn diese Transaktion mit einem Budget verbunden ist, aktualisiere das Budget
        savedTransaction.budget?.let { budget ->
            budgetService.updateBudgetWithTransaction(budget.id, savedTransaction)
        }
        
        return savedTransaction
    }
    
    // Spezielle Geschäftslogik
    
    fun getSumByType(type: TransactionType): BigDecimal = 
        transactionRepository.sumByType(type) ?: BigDecimal.ZERO
    
    fun getSumByTypeAndDateRange(type: TransactionType, start: LocalDateTime, end: LocalDateTime): BigDecimal =
        transactionRepository.sumByTypeAndTimestampBetween(type, start, end) ?: BigDecimal.ZERO
    
    // weitere Methoden...
}
```

### BudgetService

```kotlin
@Service
class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val categoryService: CategoryService
) {
    // CRUD-Operationen
    
    @Transactional
    fun updateBudgetWithTransaction(budgetId: Long, transaction: MoneyTransaction): Budget {
        val budget = getBudgetById(budgetId)
        
        // Hier würde die Logik zur Aktualisierung des Budgets implementiert
        // z.B. Überprüfung, ob das Budget überschritten wird, Auslösen von Benachrichtigungen etc.
        
        return budgetRepository.save(budget)
    }
    
    fun getBudgetForCategoryAndDate(categoryId: Long, date: LocalDate): Budget? {
        val category = categoryService.getCategoryById(categoryId)
        return budgetRepository.findByCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            category, date, date
        ).orElse(null)
    }
    
    // weitere Methoden...
}
```

## Anwendungsfälle

Die folgenden Anwendungsfälle zeigen, wie die verschiedenen Aggregate und Services zusammenarbeiten:

### Transaktion hinzufügen

1. Benutzer gibt Transaktionsdaten ein (Betrag, Beschreibung, Datum, Kategorie, Typ)
2. `TransactionDTO` validiert und konvertiert die Eingabedaten
3. `TransactionController` ruft `TransactionService.createTransaction()` auf
4. `TransactionService` speichert die Transaktion
5. Falls ein Budget verknüpft ist, wird `BudgetService.updateBudgetWithTransaction()` aufgerufen
6. Das aktualisierte Budget wird gespeichert

### Budget-Überwachung

1. Bei jeder Transaktion mit einer Kategorie, die zu einem Budget gehört, wird das Budget aktualisiert
2. Das System überwacht, ob der Schwellenwert erreicht wurde
3. Bei Erreichen des Schwellenwerts wird eine Benachrichtigung ausgelöst

## Fazit

Die Domänenmodellierung des Finance Managers folgt den DDD-Prinzipien und schafft eine klare Struktur, die die Geschäftslogik korrekt abbildet. Die Aggregate-Grenzen sind so gezogen, dass sie die natürlichen Grenzen der Domäne widerspiegeln und gleichzeitig Konsistenz und Performanz gewährleisten. 