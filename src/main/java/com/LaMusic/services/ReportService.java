package com.LaMusic.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LaMusic.dto.BestSellingProductDTO;
import com.LaMusic.dto.CategoryTrendDTO;
import com.LaMusic.dto.GrowthDTO;
import com.LaMusic.dto.LowStockProductDTO;
import com.LaMusic.dto.MonthlyRevenueProjectionDTO;
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
	
	public SalesReportDTO generateSalesReport(LocalDate start, LocalDate end){
		List<Order> orders = orderRepository.findByOrderDateBetween(start,end);
		
		int totalOrders = orders.size();
		BigDecimal totalRevenue = orders.stream()
				.map(Order::getTotalAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal averageTicket = totalOrders > 0
				?totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
				:BigDecimal.ZERO;
		
		Map<LocalDate, BigDecimal> dailySales = new TreeMap<>();
		for(Order order : orders) {
			LocalDate date = order.getOrderDate();
			dailySales.put(date, dailySales.getOrDefault(date, BigDecimal.ZERO).add(order.getTotalAmount()));
		}
		
		List<SalePerDayDTO> chart = dailySales.entrySet().stream()
				.map(e-> new SalePerDayDTO(e.getKey(), e.getValue()))
				.toList();
		
		return new SalesReportDTO(totalOrders, totalRevenue, averageTicket, chart);
	}
	
	public List<BestSellingProductDTO> getBestSellingProducts(LocalDate start, LocalDate end){
		return orderItemRepository.findBestSellingProducts(start, end);
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
	
}
