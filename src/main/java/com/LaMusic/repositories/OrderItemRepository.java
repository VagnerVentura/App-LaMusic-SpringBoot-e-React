package com.LaMusic.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.LaMusic.dto.BestSellingProductDTO;
import com.LaMusic.dto.CategoryTrendDTO;
import com.LaMusic.dto.ProductSalesReportItemDTO;
import com.LaMusic.dto.ReorderSuggestionDTO;
import com.LaMusic.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    

    @Query("""
        SELECT new com.LaMusic.dto.BestSellingProductDTO(
            oi.product.id,
            oi.product.name,
            SUM(oi.quantity),
            SUM(oi.totalPrice)
        )
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.orderDate BETWEEN :start AND :end
        AND o.status IN :statuses
        GROUP BY oi.product.id, oi.product.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<BestSellingProductDTO> findBestSellingProductsByStatus(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end,
        @Param("statuses") List<String> statuses
    );
       

     @Query("""
        SELECT new com.LaMusic.dto.ProductSalesReportItemDTO(
            oi.product.id,
            oi.product.name,
            SUM(oi.quantity),
            SUM(oi.totalPrice)
        )
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.orderDate BETWEEN :start AND :end
        GROUP BY oi.product.id, oi.product.name
        ORDER BY SUM(oi.totalPrice) DESC
    """)
    List<ProductSalesReportItemDTO> findProductSalesReportItems(@Param("start") LocalDate start, @Param("end") LocalDate end);

     
     @Query("SELECT new com.LaMusic.dto.CategoryTrendDTO(c.name, o.createdAt, SUM(oi.quantity)) " +
               "FROM OrderItem oi " +
               "JOIN oi.product p " +
               "JOIN p.categories c " +
               "JOIN oi.order o " +
               "WHERE o.createdAt BETWEEN :start AND :end " +
               "GROUP BY c.name, o.createdAt " +
               "ORDER BY c.name, o.createdAt")
        List<CategoryTrendDTO> findCategoryTrends(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
        );
     
     @Query("""
                SELECT new com.LaMusic.dto.ReorderSuggestionDTO(
                    p.id,
                    p.name,
                    p.stockQuantity,
                    SUM(oi.quantity),
                    CASE
                        WHEN p.stockQuantity < SUM(oi.quantity) THEN SUM(oi.quantity) - p.stockQuantity
                        ELSE 0
                    END
                )
                FROM OrderItem oi
                JOIN oi.product p
                JOIN oi.order o
                WHERE o.createdAt BETWEEN :start AND :end
                GROUP BY p.id, p.name, p.stockQuantity
                HAVING p.stockQuantity < SUM(oi.quantity)
                ORDER BY SUM(oi.quantity) DESC
            """)
            List<ReorderSuggestionDTO> findReorderSuggestions(@Param("start") LocalDate start, @Param("end") LocalDate end);

    
}