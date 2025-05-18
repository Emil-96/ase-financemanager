package com.emil.financemanager.model

/**
 * Data Transfer Object for Category
 * Used for form submissions and API requests
 */
class CategoryDTO {
    var id: Long? = null
    var name: String? = null
    var type: String? = null
    var description: String? = null
    var parentId: Long? = null
    
    fun toEntity(categoryService: com.emil.financemanager.service.CategoryService): Category {
        val categoryType = try {
            if (type.isNullOrBlank()) CategoryType.EXPENSE else CategoryType.valueOf(type!!)
        } catch (e: IllegalArgumentException) {
            CategoryType.EXPENSE
        }
        
        val parent = parentId?.let { categoryService.getCategoryById(it) }
        
        return Category(
            id = id ?: 0,
            name = name ?: throw IllegalArgumentException("Category name is required"),
            type = categoryType,
            description = description,
            parent = parent
        )
    }
} 