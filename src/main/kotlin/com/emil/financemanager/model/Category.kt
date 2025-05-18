package com.emil.financemanager.model

import jakarta.persistence.*

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(unique = true)
    val name: String,
    
    val type: CategoryType,
    
    val description: String? = null,
    
    @ManyToOne
    val parent: Category? = null
)

enum class CategoryType {
    INCOME, EXPENSE, SAVING
} 