package com.emil.financemanager.service

import com.emil.financemanager.model.Category
import com.emil.financemanager.model.CategoryType
import com.emil.financemanager.repository.CategoryRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class CategoryServiceTest {
    
    @Mock
    private lateinit var categoryRepository: CategoryRepository
    
    @InjectMocks
    private lateinit var categoryService: CategoryService
    
    private lateinit var testCategory: Category
    
    @BeforeEach
    fun setup() {
        testCategory = Category(
            id = 1L,
            name = "Test Category",
            type = CategoryType.EXPENSE,
            description = "Test Description"
        )
    }
    
    @Test
    fun `should return all categories`() {
        // Given
        val categories = listOf(testCategory)
        `when`(categoryRepository.findAll()).thenReturn(categories)
        
        // When
        val result = categoryService.getAllCategories()
        
        // Then
        assertEquals(categories, result)
        verify(categoryRepository).findAll()
    }
    
    @Test
    fun `should return category by id`() {
        // Given
        `when`(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory))
        
        // When
        val result = categoryService.getCategoryById(1L)
        
        // Then
        assertEquals(testCategory, result)
        verify(categoryRepository).findById(1L)
    }
    
    @Test
    fun `should throw exception when category not found`() {
        // Given
        `when`(categoryRepository.findById(99L)).thenReturn(Optional.empty())
        
        // When & Then
        val exception = assertThrows(NoSuchElementException::class.java) {
            categoryService.getCategoryById(99L)
        }
        
        assertTrue(exception.message!!.contains("not found"))
        verify(categoryRepository).findById(99L)
    }
    
    @Test
    fun `should return categories by type`() {
        // Given
        val categories = listOf(testCategory)
        `when`(categoryRepository.findByType(CategoryType.EXPENSE)).thenReturn(categories)
        
        // When
        val result = categoryService.getCategoriesByType(CategoryType.EXPENSE)
        
        // Then
        assertEquals(categories, result)
        verify(categoryRepository).findByType(CategoryType.EXPENSE)
    }
    
    @Test
    fun `should create new category`() {
        // Given
        val newCategory = testCategory.copy(id = 0L)
        `when`(categoryRepository.findByName(newCategory.name)).thenReturn(null)
        `when`(categoryRepository.save(newCategory)).thenReturn(testCategory)
        
        // When
        val result = categoryService.createCategory(newCategory)
        
        // Then
        assertEquals(testCategory, result)
        verify(categoryRepository).findByName(newCategory.name)
        verify(categoryRepository).save(newCategory)
    }
    
    @Test
    fun `should throw exception when creating category with existing name`() {
        // Given
        val newCategory = testCategory.copy(id = 0L)
        `when`(categoryRepository.findByName(newCategory.name)).thenReturn(testCategory)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            categoryService.createCategory(newCategory)
        }
        
        assertTrue(exception.message!!.contains("already exists"))
        verify(categoryRepository).findByName(newCategory.name)
        verify(categoryRepository, never()).save(any())
    }
    
    @Test
    fun `should update category`() {
        // Given
        val updatedCategory = testCategory.copy(
            name = "Updated Category",
            description = "Updated Description"
        )
        
        `when`(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory))
        `when`(categoryRepository.save(any())).thenReturn(updatedCategory)
        
        // When
        val result = categoryService.updateCategory(1L, updatedCategory)
        
        // Then
        assertEquals(updatedCategory, result)
        verify(categoryRepository).findById(1L)
        verify(categoryRepository).save(any())
    }
    
    @Test
    fun `should delete category`() {
        // Given
        `when`(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory))
        
        // When
        categoryService.deleteCategory(1L)
        
        // Then
        verify(categoryRepository).findById(1L)
        verify(categoryRepository).delete(testCategory)
    }
} 