package com.LaMusic.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.entity.Category;
import com.LaMusic.entity.Product;
import com.LaMusic.services.CategoryService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {

	private final CategoryService categoryService;
	

	@GetMapping
	public ResponseEntity<List<Category>> listCategories() {
		return ResponseEntity.ok(categoryService.listCategories());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Category> findCategoryById(@PathVariable UUID id){
		return ResponseEntity.ok(categoryService.findCategoryById(id));
	}
	
//	@PostMapping
//	public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto){
//		return ResponseEntity.ok(categoryService.createCategory(categoryDto));
//	}
	
	@PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable UUID id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCategoria(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable UUID id) {
        List<Product> products = categoryService.getProductsByCategoryId(id);
        return ResponseEntity.ok(products);
    }
	
}
