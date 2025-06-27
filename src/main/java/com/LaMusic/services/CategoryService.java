package com.LaMusic.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LaMusic.dto.ProductResponseDTO;
import com.LaMusic.entity.Category;
import com.LaMusic.entity.Product;
import com.LaMusic.entity.ProductImage;
import com.LaMusic.repositories.CategoryRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryService {

	@Autowired
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
	
	public List<ProductResponseDTO> getProductsByCategoryId(UUID categoryId) {
	    Category category = categoryRepository.findById(categoryId)
	        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

	    List<Product> products = category.getProducts();

	    return products.stream().map(product -> {
	        ProductResponseDTO dto = new ProductResponseDTO();
	        dto.setId(product.getId());
	        dto.setName(product.getName());
	        dto.setDescription(product.getDescription());
	        dto.setSlug(product.getSlug());
	        dto.setPrice(product.getPrice());

	        // Buscar imagem principal
	        String imageUrl = product.getImages() != null
	            ? product.getImages().stream()
	                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
	                .map(ProductImage::getUrl)
	                .findFirst()
	                .orElse(null)
	            : null;

	        dto.setImageUrl(imageUrl);

	        return dto;
	    }).collect(Collectors.toList());
	}
}