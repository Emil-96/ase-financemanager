package com.emil.financemanager.controller

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.CategoryDTO
import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> = 
        ResponseEntity.ok(categoryService.getAllCategories())
    
    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): ResponseEntity<Category> =
        ResponseEntity.ok(categoryService.getCategoryById(id))
    
    @GetMapping("/type/{type}")
    fun getCategoriesByType(@PathVariable type: CategoryType): ResponseEntity<List<Category>> =
        ResponseEntity.ok(categoryService.getCategoriesByType(type))
    
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createCategoryJson(@RequestBody category: Category): ResponseEntity<Category> =
        ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category))
    
    @PostMapping(consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun createCategoryForm(@ModelAttribute categoryDTO: CategoryDTO): ResponseEntity<Category> {
        val category = categoryDTO.toEntity(categoryService)
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category))
    }
    
    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @RequestBody category: Category
    ): ResponseEntity<Category> =
        ResponseEntity.ok(categoryService.updateCategory(id, category))
    
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Unit> {
        categoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }
} 