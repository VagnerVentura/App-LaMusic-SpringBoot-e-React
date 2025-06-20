package com.LaMusic.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.dto.ProductDto;
import com.LaMusic.entity.Product;
import com.LaMusic.services.ProductService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;
	
	@GetMapping
	public ResponseEntity<List<Product>> listProducts() {
		return ResponseEntity.ok(productService.listProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> findProductById(@PathVariable UUID id){
		return ResponseEntity.ok(productService.findProductById(id));
	}
	
	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto){
		return ResponseEntity.ok(productService.createProduct(productDto));
	}
	
//	@PutMapping("/{id}")
//    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product product) {
//        return ResponseEntity.ok(productService.updateProduct(id, product));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
	
}
