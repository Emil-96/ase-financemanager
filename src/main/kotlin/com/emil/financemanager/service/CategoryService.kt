package com.emil.financemanager.service

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {
    
    fun getAllCategories(): List<Category> = categoryRepository.findAll()
    
    fun getCategoryById(id: Long): Category = categoryRepository.findById(id)
        .orElseThrow { NoSuchElementException("Category not found with id: $id") }
    
    fun getCategoriesByType(type: CategoryType): List<Category> = categoryRepository.findByType(type)
    
    @Transactional
    fun createCategory(category: Category): Category {
        if (categoryRepository.findByName(category.name) != null) {
            throw IllegalArgumentException("Category with name '${category.name}' already exists")
        }
        return categoryRepository.save(category)
    }
    
    @Transactional
    fun updateCategory(id: Long, category: Category): Category {
        val existingCategory = getCategoryById(id)
        
        val updated = existingCategory.copy(
            name = category.name,
            type = category.type,
            description = category.description,
            parent = category.parent
        )
        
        return categoryRepository.save(updated)
    }
    
    @Transactional
    fun deleteCategory(id: Long) {
        val category = getCategoryById(id)
        categoryRepository.delete(category)
    }
} 