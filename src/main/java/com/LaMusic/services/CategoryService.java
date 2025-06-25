package com.LaMusic.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.LaMusic.entity.Category;
import com.LaMusic.entity.Product;
import com.LaMusic.repositories.CategoryRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryService {

	CategoryRepository categoryRepository;
	
	public List<Category> listCategories(){
		return categoryRepository.findAll();
	}
	
	public Category findCategoryById(UUID id) {
		return categoryRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("Category not found!")) ;	
	}
	
//	public Category createCategory(CategoryDTO categoryDto) {
//		return categoryRepository.save(categoryDto.toCategory());
//	}
	
	public Category updateCategory(UUID id, Category updatedCategory) {
        Category existingCategory = findCategoryById(id);
        existingCategory.setName(updatedCategory.getName());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

	public List<Category> getCategoriesByIds(List<UUID> list ) {
		return categoryRepository.findAllById(list);
	}
	
	public List<Product> getProductsByCategoryId(UUID categoryId) {
	        Category category = categoryRepository.findById(categoryId)
	            .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + categoryId));
	        return category.getProducts();
	}
}