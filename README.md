# Personal Finance Manager

A comprehensive application for managing personal finances, built with Spring Boot and Kotlin.

## Features

- **Transaction Management**: Track your income and expenses with detailed categorization
- **Budget Planning**: Create and monitor budgets for different expense categories
- **Saving Goals**: Set saving targets and track your progress
- **Financial Reports**: Generate reports on income, expenses, and savings
- **Transaction Import**: Import transactions from CSV files
- **Notifications**: Get alerts when budgets are nearing their limits

## Technologies Used

- Kotlin 1.9.25
- Spring Boot 3.4.5
- Spring Data JPA
- PostgreSQL (for production)
- H2 Database (for development)
- Mustache templating
- Bootstrap 5 for UI

## Architecture

This project follows Domain-Driven Design (DDD) principles and Clean Architecture:

- **Domain Layer**: Core business entities and logic
- **Repository Layer**: Data access interfaces
- **Service Layer**: Business logic implementation
- **Controller Layer**: HTTP endpoints and web UI controllers

## Project Structure

```
src/
├── main/
│   ├── kotlin/com/emil/financemanager/
│   │   ├── controller/    # Web and API controllers
│   │   ├── model/         # Domain entities
│   │   ├── repository/    # Data access interfaces
│   │   ├── service/       # Business logic services
│   │   └── config/        # Application configuration
│   └── resources/
│       ├── templates/     # Mustache templates
│       ├── static/        # Static resources (CSS, JS)
│       └── application.properties  # Application configuration
└── test/
    └── kotlin/com/emil/financemanager/
        └── service/       # Unit tests for services
```

## Setup and Installation

### Prerequisites

- JDK 17 or higher
- PostgreSQL (for production)
- Gradle

### Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE financemanager;
```

2. Update the database configuration in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/financemanager
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Running the Application

1. Clone the repository:

```bash
git clone https://github.com/yourusername/finance-manager.git
cd finance-manager
```

2. Build the application:

```bash
./gradlew build
```

3. Run the application:

```bash
./gradlew bootRun
```

The application will be available at http://localhost:8080

## Testing

Run the automated tests with:

```bash
./gradlew test
```

## License

This project is licensed under the MIT License. 