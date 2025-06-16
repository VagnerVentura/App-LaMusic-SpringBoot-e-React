package com.LaMusic.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.LaMusic.dto.BestSellingProductDTO;
import com.LaMusic.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
	
	 @Query("""
		        SELECT new com.LaMusic.dto.BestSellingProductDTO(
		            oi.product.id,
		            oi.product.name,
		            SUM(oi.quantity)
		        )
		        FROM OrderItem oi
		        WHERE oi.order.orderDate BETWEEN :start AND :end
		        GROUP BY oi.product.id, oi.product.name
		        ORDER BY SUM(oi.quantity) DESC
		    """)
		    List<BestSellingProductDTO> findBestSellingProducts(
		        @Param("start") LocalDate start,
		        @Param("end") LocalDate end
		    );
	
}
