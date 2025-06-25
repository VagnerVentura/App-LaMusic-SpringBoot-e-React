package com.LaMusic.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LaMusic.dto.BestSellingProductDTO;
import com.LaMusic.dto.BestSellingProductsReportDTO;
import com.LaMusic.dto.CategoryTrendDTO;
import com.LaMusic.dto.GrowthDTO;
import com.LaMusic.dto.InactiveCustomerDTO;
import com.LaMusic.dto.LowStockProductDTO;
import com.LaMusic.dto.MonthlyRevenueProjectionDTO;
import com.LaMusic.dto.MonthlyUserSignupDTO;
import com.LaMusic.dto.ProductSalesReportDTO;
import com.LaMusic.dto.ProductSalesReportItemDTO;
import com.LaMusic.dto.ReorderSuggestionDTO;
import com.LaMusic.dto.SalePerDayDTO;
import com.LaMusic.dto.SalesComparisonDTO;
import com.LaMusic.dto.SalesPeriodDTO;
import com.LaMusic.dto.SalesReportDTO;
import com.LaMusic.dto.SalesSummaryDTO;
import com.LaMusic.dto.SalesSummaryRawDTO;
import com.LaMusic.dto.TopCustomerDTO;
import com.LaMusic.entity.Order;
import com.LaMusic.repositories.OrderItemRepository;
import com.LaMusic.repositories.OrderRepository;
import com.LaMusic.repositories.ProductRepository;
import com.LaMusic.repositories.UserRepository;
import com.LaMusic.dto.AdvancedSalesReportDTO;
import com.LaMusic.dto.DailySalesStatusDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	@Autowired
	private final OrderRepository orderRepository;

	@Autowired
	private final OrderItemRepository orderItemRepository;
	
	@Autowired
	private final ProductRepository productRepository;
	
	@Autowired
	private final UserRepository userRepository;
	
    public AdvancedSalesReportDTO generateSalesReport(LocalDate start, LocalDate end){
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);

        BigDecimal totalCompletedRevenue = BigDecimal.ZERO;
        int totalCompletedOrders = 0;

        BigDecimal totalPendingRevenue = BigDecimal.ZERO;
        int totalPendingOrders = 0;

        // Usamos um TreeMap para manter as datas ordenadas automaticamente.
        Map<LocalDate, DailySalesStatusDTO> dailySales = new TreeMap<>();

        for (Order order : orders) {
            LocalDate date = order.getOrderDate();
            // Garante que existe uma entrada para cada dia com pedido.
            dailySales.putIfAbsent(date, new DailySalesStatusDTO(date));
            DailySalesStatusDTO dayData = dailySales.get(date);

            // Assumindo que status "completed", "shipped", "delivered" são vendas finalizadas.
            // Ajuste os status conforme a sua regra de negócio.
            if ("COMPLETED".equalsIgnoreCase(order.getStatus()) || "SHIPPED".equalsIgnoreCase(order.getStatus()) || "DELIVERED".equalsIgnoreCase(order.getStatus())) {
                totalCompletedRevenue = totalCompletedRevenue.add(order.getTotalAmount());
                totalCompletedOrders++;
                dayData.setCompletedAmount(dayData.getCompletedAmount().add(order.getTotalAmount()));
            } else { // Todos os outros status (pending, canceled, etc.)
                totalPendingRevenue = totalPendingRevenue.add(order.getTotalAmount());
                totalPendingOrders++;
                dayData.setPendingAmount(dayData.getPendingAmount().add(order.getTotalAmount()));
            }
        }

        BigDecimal averageCompletedTicket = totalCompletedOrders > 0
                ? totalCompletedRevenue.divide(BigDecimal.valueOf(totalCompletedOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<DailySalesStatusDTO> chart = new ArrayList<>(dailySales.values());

        return new AdvancedSalesReportDTO(
                totalCompletedOrders,
                totalCompletedRevenue,
                averageCompletedTicket,
                totalPendingOrders,
                totalPendingRevenue,
                chart
        );
    }
	    public ProductSalesReportDTO generateProductSalesReport(LocalDate start, LocalDate end) {
        List<ProductSalesReportItemDTO> items = orderItemRepository.findProductSalesReportItems(start, end);

        BigDecimal totalRevenue = items.stream()
            .map(ProductSalesReportItemDTO::getTotalRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalQuantitySold = items.stream()
            .mapToLong(ProductSalesReportItemDTO::getQuantitySold)
            .sum();

        return new ProductSalesReportDTO(items, totalQuantitySold, totalRevenue);
    }
	
    public BestSellingProductsReportDTO getBestSellingProducts(LocalDate start, LocalDate end){
        List<String> completedStatuses = List.of("COMPLETED", "SHIPPED", "DELIVERED");
        List<String> pendingStatuses = List.of("PENDING");

        List<BestSellingProductDTO> completedProducts = orderItemRepository.findBestSellingProductsByStatus(start, end, completedStatuses);
        List<BestSellingProductDTO> pendingProducts = orderItemRepository.findBestSellingProductsByStatus(start, end, pendingStatuses);

        return new BestSellingProductsReportDTO(completedProducts, pendingProducts);
    }
	
	public List<LowStockProductDTO> getLowStockProducts(Integer threslhold){
		return productRepository.findLowStockProducts(threslhold); 
	}
	
	public List<TopCustomerDTO> getTopCustomers(LocalDate start, LocalDate end) {
	    return orderRepository.findTopCustomers(start, end);
	}
	
	public SalesSummaryDTO getSalesSummary(LocalDate start, LocalDate end) {
		return orderRepository.getSalesSummary(start, end);
	}	
		
	public SalesSummaryDTO getSalesSummaryRaw(LocalDate start, LocalDate end) {
		SalesSummaryRawDTO raw = orderRepository.getRawSalesSummary(start, end);
		BigDecimal average = BigDecimal.ZERO;
		
		if(raw.getTotalOrders() > 0) {
			average = raw.getTotalRevenue().divide(BigDecimal.valueOf(raw.getTotalOrders()),2,RoundingMode.HALF_UP);
		}		
		return new SalesSummaryDTO(raw.getTotalRevenue(), raw.getTotalOrders(),average);
	}	
	
	public SalesComparisonDTO getSalesComparison(LocalDate start1,LocalDate end1, LocalDate start2, LocalDate end2) {
		SalesPeriodDTO p1 = orderRepository.getSalesPeriodSummary(start1, end1);
		SalesPeriodDTO p2 = orderRepository.getSalesPeriodSummary(start2, end2);
		
		GrowthDTO growth = new GrowthDTO(
				calculateGrowth(p1.getTotalRevenue(), p2.getTotalRevenue()),
		        calculateGrowth(BigDecimal.valueOf(p1.getTotalOrders()), BigDecimal.valueOf(p2.getTotalOrders())),
		        calculateGrowth(p1.getAverageTicket(), p2.getAverageTicket())
		);
			
		return new SalesComparisonDTO(p1,p2, growth);
	}
	
	private Double calculateGrowth(BigDecimal oldValue, BigDecimal newValue) {
		 if (oldValue == null || oldValue.compareTo(BigDecimal.ZERO) == 0) {
		        return newValue.compareTo(BigDecimal.ZERO) == 0 ? 0.0 : 100.0;
		    }
		    return newValue.subtract(oldValue)
		                   .divide(oldValue, 4, RoundingMode.HALF_UP)
		                   .multiply(BigDecimal.valueOf(100))
		                   .doubleValue();
	}
	
	public List<CategoryTrendDTO> getCategoryTrends(LocalDate start, LocalDate end) {
	    return orderItemRepository.findCategoryTrends(start, end);
	}
	
	public List<ReorderSuggestionDTO> getReorderSuggestions(LocalDate start, LocalDate end) {
	    return orderItemRepository.findReorderSuggestions(start, end);
	}
	
	public List<MonthlyRevenueProjectionDTO> getRevenueProjection(int monthsBack, int monthsAhead) {
	    LocalDate now = LocalDate.now();
	    LocalDate start = now.minusMonths(monthsBack).withDayOfMonth(1);
	    LocalDate end = now.withDayOfMonth(1).minusDays(1);

	    List<MonthlyRevenueProjectionDTO> historico = orderRepository.getMonthlyRevenues(start, end);

	    // calcular média dos últimos X meses
	    BigDecimal media = historico.stream()
	        .map(MonthlyRevenueProjectionDTO::getRevenue)
	        .reduce(BigDecimal.ZERO, BigDecimal::add)
	        .divide(new BigDecimal(historico.size()), 2, RoundingMode.HALF_UP);

	    List<MonthlyRevenueProjectionDTO> resultado = new ArrayList<>(historico);

	    YearMonth atual = YearMonth.from(LocalDate.now());

	    for (int i = 1; i <= monthsAhead; i++) {
	        YearMonth mesFuturo = atual.plusMonths(i);

	        // Exemplo de sazonalidade simples
	        BigDecimal projetado = media;
	        if (mesFuturo.getMonthValue() == 12) {
	            projetado = projetado.multiply(BigDecimal.valueOf(1.4)); // Dezembro
	        } else if (mesFuturo.getMonthValue() == 1) {
	            projetado = projetado.multiply(BigDecimal.valueOf(0.9)); // Janeiro
	        }

	        int mesFormatado = mesFuturo.getYear() * 100 + mesFuturo.getMonthValue();

	        resultado.add(new MonthlyRevenueProjectionDTO(
	            mesFormatado,
	            projetado.setScale(2, RoundingMode.HALF_UP),
	            true
	        ));
	    }

	    return resultado;
	}
	
	public List<InactiveCustomerDTO> getInactiveCustomers(int months) {
	    LocalDate cutoffDate = LocalDate.now().minusMonths(months);
	    return userRepository.findInactiveCustomers(cutoffDate);
	}
	
	public List<MonthlyUserSignupDTO> getMonthlyUserSignups(LocalDate start, LocalDate end) {
	    // Converte LocalDate para OffsetDateTime no fuso UTC
	    OffsetDateTime startDateTime = start.atStartOfDay().atOffset(ZoneOffset.UTC);
	    OffsetDateTime endDateTime = end.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

	    List<Object[]> rawData = userRepository.rawUserSignups(startDateTime, endDateTime);

	    return rawData.stream()
	        .map(obj -> {
	            Integer yearMonth = (Integer) obj[0];
	            Long count = (Long) obj[1];

	            int year = yearMonth / 100;
	            int month = yearMonth % 100;

	            return new MonthlyUserSignupDTO(YearMonth.of(year, month), count);
	        })
	        .collect(Collectors.toList());
	}

	
}
