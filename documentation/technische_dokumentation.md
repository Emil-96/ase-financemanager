# Technische Dokumentation: Finance Manager

## 1. Domain Driven Design (DDD)

### Analyse der Ubiquitous Language

Die Ubiquitous Language (allgegenwärtige Sprache) des Finance Managers umfasst folgende Kernbegriffe:

- **Transaktion (MoneyTransaction)**: Repräsentiert einen Geldfluss, entweder als Einnahme oder Ausgabe.
- **Kategorie (Category)**: Klassifizierung von Transaktionen wie "Gehalt", "Lebensmittel", "Freizeit" etc.
- **Budget**: Ein für eine Kategorie festgelegter Betrag, der in einem bestimmten Zeitraum ausgegeben werden darf.
- **Sparziel (SavingGoal)**: Ein finanzielles Ziel, für das Geld zurückgelegt wird.
- **Bericht (Report)**: Übersicht über finanzielle Aktivitäten in einem bestimmten Zeitraum.

Die konsistente Verwendung dieser Begriffe sowohl im Code als auch in der Benutzeroberfläche stellt sicher, dass alle Beteiligten (Entwickler, Benutzer) ein gemeinsames Verständnis der Domäne haben.

### Verwendung taktischer Muster des DDD

#### Value Objects

Value Objects sind unveränderliche Objekte, die durch ihre Attribute definiert werden. In unserem Projekt sind dies:

- **TransactionType**: Ein Enum, das den Typ einer Transaktion als INCOME oder EXPENSE definiert.
```kotlin
enum class TransactionType {
    INCOME, EXPENSE
}
```

- **CategoryType**: Ein Enum, das die Art der Kategorie definiert.
```kotlin
enum class CategoryType {
    INCOME, EXPENSE, SAVING
}
```

Diese Value Objects haben keine eigene Identität, sondern werden durch ihre Werte definiert.

#### Entities

Entities sind Objekte mit einer eindeutigen Identität, die über ihren Lebenszyklus erhalten bleibt:

- **MoneyTransaction**: Eine Entity, die eine einzelne finanzielle Transaktion repräsentiert.
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

- **Category**: Eine Entity, die eine Kategorie für Transaktionen definiert.
- **Budget**: Eine Entity, die ein Budget für eine bestimmte Kategorie in einem Zeitraum definiert.
- **SavingGoal**: Eine Entity, die ein Sparziel repräsentiert.

#### Aggregates

Aggregate sind Cluster von Entities und Value Objects, die als eine Einheit behandelt werden:

- **Transaction Aggregate**: Besteht aus `MoneyTransaction` als Root Entity, verknüpft mit `Category` und optional `Budget`.
- **Budget Aggregate**: Besteht aus `Budget` als Root Entity, enthält Referenzen zu den zugehörigen `MoneyTransaction` Entities.
- **SavingGoal Aggregate**: Besteht aus `SavingGoal` als Root Entity, verknüpft mit den entsprechenden Transaktionen.

#### Repositories

Repositories kapseln die Logik für den Zugriff auf die Persistenzschicht:

- **TransactionRepository**: Verwaltet den Zugriff auf `MoneyTransaction` Entities.
```kotlin
@Repository
interface TransactionRepository : JpaRepository<MoneyTransaction, Long> {
    fun findByType(type: TransactionType): List<MoneyTransaction>
    fun findByCategory(category: Category): List<MoneyTransaction>
    fun findByTimestampBetween(start: LocalDateTime, end: LocalDateTime): List<MoneyTransaction>
    
    @Query("SELECT SUM(t.amount) FROM MoneyTransaction t WHERE t.type = :type")
    fun sumByType(type: TransactionType): BigDecimal?
    
    // weitere Methoden...
}
```

- **CategoryRepository**: Verwaltet den Zugriff auf `Category` Entities.
- **BudgetRepository**: Verwaltet den Zugriff auf `Budget` Entities.
- **SavingGoalRepository**: Verwaltet den Zugriff auf `SavingGoal` Entities.

#### Domain Services

Domain Services implementieren Geschäftslogik, die nicht natürlich zu einer einzelnen Entity gehört:

- **TransactionService**: Verwaltet Transaktionen und implementiert domänenspezifische Logik.
```kotlin
@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryService: CategoryService,
    private val budgetService: BudgetService
) {
    // Methoden zur Verwaltung von Transaktionen
    
    @Transactional
    fun createTransaction(transaction: MoneyTransaction): MoneyTransaction {
        val savedTransaction = transactionRepository.save(transaction)
        
        // If this transaction is connected to a budget, update the budget
        savedTransaction.budget?.let { budget ->
            budgetService.updateBudgetWithTransaction(budget.id, savedTransaction)
        }
        
        return savedTransaction
    }
    
    // weitere Methoden...
}
```

- **BudgetService**: Implementiert Logik für die Budgetverwaltung und -überwachung.
- **SavingGoalService**: Implementiert Logik für die Verwaltung von Sparzielen.

### Analyse und Begründung der verwendeten Muster

Die Verwendung von DDD-Mustern in diesem Projekt hat mehrere Vorteile:

1. **Klare Domänenmodellierung**: Die Kernkonzepte der Finanzverwaltung (Transaktionen, Kategorien, Budgets, Sparziele) sind klar als Entities oder Value Objects modelliert.
2. **Trennung von Zuständigkeiten**: Die Repositories trennen die Datenzugriffslogik von der Geschäftslogik, die in Domain Services implementiert ist.
3. **Konsistente Sprache**: Die Verwendung einer einheitlichen Ubiquitous Language verbessert die Kommunikation im Team und die Verständlichkeit des Codes.
4. **Geschäftsregelinvarianz**: Durch die Definition von Aggregates wird sichergestellt, dass Geschäftsregeln konsistent angewendet werden und Datenintegrität gewahrt bleibt.

Die Aggregates wurden so gestaltet, dass sie die Konsistenzgrenzen der Anwendung respektieren. Zum Beispiel wird bei der Erstellung einer Transaktion, die zu einem Budget gehört, das Budget automatisch aktualisiert, wie im `TransactionService.createTransaction` zu sehen ist.

## 2. Clean Architecture

### Schichtarchitektur

Unser Finance Manager folgt den Prinzipien der Clean Architecture mit einer klaren Schichtentrennung:

1. **Domain Layer (Innerste Schicht)**
   - Enthält die Kernentitäten und Geschäftsregeln
   - Unabhängig von externen Frameworks und Bibliotheken
   - Beispiel: Die Basisklassen in `com.emil.financemanager.model`

2. **Application Layer (Service Layer)**
   - Implementiert die Anwendungsfälle und orchestriert den Domänen-Layer
   - Enthält die Geschäftslogik der Anwendung
   - Beispiel: Services in `com.emil.financemanager.service`

3. **Adapter Layer (Controller & Repository)**
   - Verbindet die Anwendung mit externen Systemen wie Datenbank und UI
   - Controller vermitteln zwischen UI und Application Layer
   - Repositories vermitteln zwischen Application Layer und Datenpersistenz
   - Beispiele: 
     - Controller in `com.emil.financemanager.controller`
     - Repositories in `com.emil.financemanager.repository`

4. **Framework & Driver Layer (Äußerste Schicht)**
   - Enthält Frameworks und Tools wie Spring Boot, Thymeleaf, PostgreSQL
   - Implementiert die technischen Details
   - Beispiele: Konfigurationsklassen, Templates

### Begründung der Schichtarchitektur

Diese Architektur wurde gewählt, um:

1. **Testbarkeit zu verbessern**: Die klare Trennung der Schichten ermöglicht Unit-Tests ohne Abhängigkeiten zu externen Systemen.
2. **Wartbarkeit zu erhöhen**: Änderungen in einer Schicht (z.B. Wechsel der Datenbank) beeinflussen die anderen Schichten nicht.
3. **Entwicklungsgeschwindigkeit zu steigern**: Mehrere Entwickler können parallel an verschiedenen Schichten arbeiten.
4. **Flexibilität zu gewährleisten**: Die UI oder Datenbank kann ausgetauscht werden, ohne den Domänen-Code zu ändern.

Ein konkretes Beispiel für die Umsetzung der Clean Architecture ist der Transaktions-Flow:

- Der `TransactionController` nimmt Anfragen vom Frontend entgegen
- Er delegiert an den `TransactionService`, der die Geschäftslogik enthält
- Der Service nutzt das `TransactionRepository` für Datenbankoperationen
- Die eigentliche Domänenlogik ist in der `MoneyTransaction`-Entity gekapselt

```kotlin
// Controller-Schicht
@PostMapping(consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
fun createTransactionForm(@ModelAttribute transactionDTO: TransactionDTO): ResponseEntity<MoneyTransaction> {
    val transaction = transactionDTO.toEntity(categoryService, budgetService)
    return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transaction))
}

// Service-Schicht
@Transactional
fun createTransaction(transaction: MoneyTransaction): MoneyTransaction {
    val savedTransaction = transactionRepository.save(transaction)
    
    // Domain-Logik
    savedTransaction.budget?.let { budget ->
        budgetService.updateBudgetWithTransaction(budget.id, savedTransaction)
    }
    
    return savedTransaction
}

// Repository-Schicht
interface TransactionRepository : JpaRepository<MoneyTransaction, Long> {
    // Datenbankzugriffsmethoden
}
```

## 3. Programming Principles

### SOLID-Prinzipien

#### Single Responsibility Principle (SRP)

Jede Klasse hat eine klar definierte Verantwortung:

- `TransactionService`: Verantwortlich für Transaktionslogik
- `CategoryService`: Verantwortlich für Kategorieverwaltung
- `BudgetService`: Verantwortlich für Budgetverwaltung

Beispiel:
```kotlin
@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryService: CategoryService,
    private val budgetService: BudgetService
) {
    // Nur Methoden zur Verwaltung von Transaktionen
}
```

#### Open/Closed Principle (OCP)

Die Anwendung ist für Erweiterungen offen, aber gegen Modifikationen geschlossen.

Beispiel: Die Kategoriehierarchie kann um neue Kategorietypen erweitert werden, ohne den bestehenden Code zu ändern:

```kotlin
enum class CategoryType {
    INCOME, EXPENSE, SAVING
    // Neue Typen können hinzugefügt werden, ohne bestehenden Code zu ändern
}
```

#### Liskov Substitution Principle (LSP)

Unterklassen können anstelle ihrer Basisklassen verwendet werden, ohne das Verhalten zu ändern.

In unserem Projekt verwenden wir hauptsächlich Interfaces (z.B. Repository-Interfaces), die verschiedene Implementierungen haben können.

```kotlin
interface TransactionRepository : JpaRepository<MoneyTransaction, Long> {
    // Methoden definieren ein Kontraktverhalten
}
```

#### Interface Segregation Principle (ISP)

Clients sollten nicht von Methoden abhängig sein, die sie nicht verwenden.

Unsere Repositories definieren spezifische Methoden für ihre Clients und zwingen keine unnötigen Abhängigkeiten auf:

```kotlin
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByType(type: CategoryType): List<Category>
    // Nur die benötigten Methoden
}
```

#### Dependency Inversion Principle (DIP)

Module höherer Ebenen sollten nicht von Modulen niedrigerer Ebenen abhängen, sondern beide sollten von Abstraktionen abhängen.

In unserem Projekt injizieren wir Abhängigkeiten über Konstruktoren und verwenden Interfaces statt konkreter Implementierungen:

```kotlin
@Service
class TransactionService(
    private val transactionRepository: TransactionRepository, // Abhängigkeit von einer Abstraktion
    private val categoryService: CategoryService,
    private val budgetService: BudgetService
) {
    // Implementierung
}
```

### DRY (Don't Repeat Yourself)

Wir vermeiden Code-Duplizierung durch Abstraktion gemeinsamer Funktionalität.

Beispiel: Die `TransactionDTO`-Klasse kapselt die Konvertierungslogik von DTO zu Entity, anstatt diese Logik in mehreren Controllers zu duplizieren:

```kotlin
class TransactionDTO {
    // Attribute
    
    fun toEntity(categoryService: CategoryService, budgetService: BudgetService?): MoneyTransaction {
        // Konvertierungslogik
    }
}
```

## 4. Unit Tests

### ATRIP-Regeln

Unsere Unit-Tests folgen den ATRIP-Regeln (Automatic, Thorough, Repeatable, Independent, Professional):

- **Automatic**: Tests werden automatisch durch das Build-System ausgeführt
- **Thorough**: Tests decken verschiedene Anwendungsfälle und Edge Cases ab
- **Repeatable**: Tests liefern konsistente Ergebnisse bei jedem Durchlauf
- **Independent**: Tests sind voneinander unabhängig
- **Professional**: Tests sind gut strukturiert und wartbar

Beispiel eines Unit-Tests:

```kotlin
@Test
fun `should create transaction and update budget`() {
    // Arrange
    val category = Category(name = "Test", type = CategoryType.EXPENSE)
    val budget = Budget(
        name = "Test Budget",
        category = category,
        amount = BigDecimal("100"),
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusMonths(1)
    )
    val transaction = MoneyTransaction(
        amount = BigDecimal("50"),
        description = "Test Transaction",
        category = category,
        type = TransactionType.EXPENSE,
        budget = budget
    )
    
    // Mock-Verhalten
    whenever(transactionRepository.save(any())).thenReturn(transaction)
    whenever(budgetService.updateBudgetWithTransaction(anyLong(), any())).thenReturn(budget)
    
    // Act
    val result = transactionService.createTransaction(transaction)
    
    // Assert
    assertEquals(transaction, result)
    verify(transactionRepository).save(transaction)
    verify(budgetService).updateBudgetWithTransaction(budget.id, transaction)
}
```

### Einsatz von Mocks

Wir verwenden Mockito, um externe Abhängigkeiten in Unit-Tests zu mocken:

```kotlin
@MockBean
private lateinit var transactionRepository: TransactionRepository

@MockBean
private lateinit var categoryService: CategoryService

@MockBean
private lateinit var budgetService: BudgetService

@Test
fun `should get transactions by type`() {
    // Arrange
    val type = TransactionType.EXPENSE
    val transactions = listOf(
        MoneyTransaction(
            amount = BigDecimal("50"),
            description = "Test",
            category = Category(name = "Food", type = CategoryType.EXPENSE),
            type = type
        )
    )
    
    whenever(transactionRepository.findByType(type)).thenReturn(transactions)
    
    // Act
    val result = transactionService.getTransactionsByType(type)
    
    // Assert
    assertEquals(transactions, result)
    verify(transactionRepository).findByType(type)
}
```

## 5. Refactoring

### Code Smells

In der initialen Implementierung wurden folgende Code Smells identifiziert:

1. **Primitive Obsession**: Überall wurden primitive Typen verwendet, anstatt aussagekräftige Value Objects.
2. **Duplicate Code**: In mehreren Controllers gab es ähnliche Logik zur Validierung und Transformation von Eingabedaten.
3. **Large Class**: Einige Service-Klassen enthielten zu viele Verantwortlichkeiten.

### Durchgeführte Refactorings

#### 1. Einführung eines TransactionDTO

**Vorher**:
```kotlin
// Code verteilt in verschiedenen Controller-Methoden
val amount = BigDecimal(amountStr)
val categoryEntity = categoryService.getCategoryById(categoryId)
// weitere Transformationen...
```

**Nachher**:
```kotlin
// Konsolidiert in TransactionDTO
class TransactionDTO {
    var id: Long? = null
    var amount: String? = null
    var description: String? = null
    var date: String? = null
    var categoryId: String? = null
    var type: String? = null
    var budgetId: String? = null
    
    fun toEntity(categoryService: CategoryService, budgetService: BudgetService?): MoneyTransaction {
        // Transformationslogik zentralisiert
    }
}
```

**Begründung**: Dieses Refactoring verbessert die Kohäsion, reduziert Duplizierung und macht den Code wartbarer. Die Transformationslogik ist nun an einem zentralen Ort.

#### 2. Aufteilung des FinanceService

**Vorher**:
```kotlin
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

    // weitere Methoden...
}
```

**Nachher**: Aufgeteilt in mehrere spezialisierte Services wie `TransactionService`, `BudgetService` etc.

**Begründung**: Dieses Refactoring verbessert die Einhaltung des Single Responsibility Principle und macht die Anwendungsstruktur klarer.

## 6. Entwurfsmuster

### Repository Pattern

In unserem Projekt setzen wir das Repository-Muster ein, um den Datenzugriff zu abstrahieren:

```kotlin
@Repository
interface TransactionRepository : JpaRepository<MoneyTransaction, Long> {
    fun findByType(type: TransactionType): List<MoneyTransaction>
    fun findByCategory(category: Category): List<MoneyTransaction>
    // weitere Methoden...
}
```

**Begründung für den Einsatz**:

1. **Abstraktion der Datenpersistenz**: Das Repository-Muster abstrahiert die Details der Datenspeicherung, sodass die Geschäftslogik nicht wissen muss, wie Daten gespeichert werden.
2. **Testbarkeit**: Durch die Abstraktion können wir Repositories in Tests leicht mocken, was die Testbarkeit erhöht.
3. **Flexibilität**: Bei Änderungen der Datenpersistenz (z.B. Wechsel von PostgreSQL zu MongoDB) muss nur die Repository-Implementierung angepasst werden.
4. **Trennung der Zuständigkeiten**: Die Verantwortung für den Datenzugriff ist klar vom Rest der Anwendung getrennt.

Das Repository-Muster passt perfekt zu unserem DDD-Ansatz, da es uns ermöglicht, die Persistenz von der Domäne zu trennen und gleichzeitig eine klare Schnittstelle für den Zugriff auf Aggregate zu bieten.

## Fazit

Die Implementierung des Finance Managers folgt bewährten Architektur- und Design-Prinzipien wie Domain-Driven Design und Clean Architecture. Durch die konsequente Anwendung dieser Prinzipien haben wir eine gut strukturierte, testbare und wartbare Anwendung geschaffen, die die Anforderungen der Benutzer erfüllt und gleichzeitig ein solides Fundament für zukünftige Erweiterungen bietet. 