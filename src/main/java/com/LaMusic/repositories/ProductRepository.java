package com.LaMusic.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.LaMusic.dto.LowStockProductDTO;
import com.LaMusic.entity.Product;

public interface ProductRepository extends JpaRepository <Product, UUID>{

	@Query("""
			SELECT new com.LaMusic.dto.LowStockProductDTO(
				p.id, p.name, p.stockQuantity
		)
			FROM Product p
			WHERE p.stockQuantity <= :threshold
			ORDER BY p.stockQuantity ASC			
		""")
	 List<LowStockProductDTO> findLowStockProducts(@Param("threshold") Integer threshold);	
	
}
