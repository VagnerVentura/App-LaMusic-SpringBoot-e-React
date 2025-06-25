package com.LaMusic.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.LaMusic.dto.MonthlyRevenueProjectionDTO;
import com.LaMusic.dto.SalesPeriodDTO;
import com.LaMusic.dto.SalesSummaryDTO;
import com.LaMusic.dto.SalesSummaryRawDTO;
import com.LaMusic.dto.TopCustomerDTO;
import com.LaMusic.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

	List<Order> findByUserId(UUID userId);
	
    List<Order> findByOrderDateBetween(LocalDate start, LocalDate end);

    @Query("""
            SELECT new com.LaMusic.dto.SalesSummaryDTO(
                COALESCE(SUM(o.totalAmount), 0),
                COUNT(o),
                CASE WHEN COUNT(o) = 0 THEN 0 ELSE SUM(o.totalAmount) / COUNT(o) END
            )
            FROM Order o
            WHERE o.createdAt BETWEEN :start AND :end
        """)
    SalesSummaryDTO getSalesSummary(
    		@Param("start") LocalDate start,
    		@Param("end") LocalDate end
    		);
    
    
    @Query("""
    	    SELECT new com.LaMusic.dto.SalesSummaryRawDTO(
    	        COALESCE(SUM(o.totalAmount), 0),
    	        COUNT(o)
    	    )
    	    FROM Order o
    	    WHERE o.createdAt BETWEEN :start AND :end
    	""")
	SalesSummaryRawDTO getRawSalesSummary(
    	    @Param("start") LocalDate start,
    	    @Param("end") LocalDate end
    	);
    
    
    @Query("""
    	    SELECT new com.LaMusic.dto.SalesPeriodDTO(
    	        COALESCE(SUM(o.totalAmount), 0),
    	        COUNT(o),
    	        CASE WHEN COUNT(o) = 0 THEN 0 ELSE SUM(o.totalAmount) / COUNT(o) END
    	    )
    	    FROM Order o
    	    WHERE o.createdAt BETWEEN :start AND :end
    	""")
    	SalesPeriodDTO getSalesPeriodSummary(@Param("start") LocalDate start, @Param("end") LocalDate end);
    
    
    @Query("""
            SELECT new com.LaMusic.dto.TopCustomerDTO(
                o.user.id,
                o.user.name,
                o.user.email,
                COUNT(o) AS orderCount,
                SUM(o.totalAmount) AS totalSpent
            )
            FROM Order o
            WHERE o.createdAt BETWEEN :start AND :end
            GROUP BY o.user.id, o.user.name, o.user.email
            ORDER BY SUM(o.totalAmount) DESC
        """)
        List<TopCustomerDTO> findTopCustomers(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
        );
    
    @Query("""
    	    SELECT new com.LaMusic.dto.MonthlyRevenueProjectionDTO(
    	        EXTRACT(YEAR FROM o.orderDate) * 100 + EXTRACT(MONTH FROM o.orderDate),
    	        SUM(o.totalAmount),
    	        false
    	    )
    	    FROM Order o
    	    WHERE o.orderDate BETWEEN :start AND :end
    	    GROUP BY EXTRACT(YEAR FROM o.orderDate), EXTRACT(MONTH FROM o.orderDate)
    	    ORDER BY EXTRACT(YEAR FROM o.orderDate), EXTRACT(MONTH FROM o.orderDate)
    	""")
    	List<MonthlyRevenueProjectionDTO> getMonthlyRevenues(
    	    @Param("start") LocalDate start,
    	    @Param("end") LocalDate end
    	);
    
}
