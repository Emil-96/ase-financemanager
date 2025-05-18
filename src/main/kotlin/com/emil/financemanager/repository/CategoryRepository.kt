package com.emil.financemanager.repository

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.CategoryType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Category?
    fun findByType(type: CategoryType): List<Category>
} 