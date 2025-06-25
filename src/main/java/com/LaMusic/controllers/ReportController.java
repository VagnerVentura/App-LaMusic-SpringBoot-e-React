package com.LaMusic.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.dto.BestSellingProductDTO;
import com.LaMusic.dto.BestSellingProductsReportDTO;
import com.LaMusic.dto.CategoryTrendDTO;
import com.LaMusic.dto.InactiveCustomerDTO;
import com.LaMusic.dto.LowStockProductDTO;
import com.LaMusic.dto.MonthlyRevenueProjectionDTO;
import com.LaMusic.dto.MonthlyUserSignupDTO;
import com.LaMusic.dto.ReorderSuggestionDTO;
import com.LaMusic.dto.SalesComparisonDTO;
import com.LaMusic.dto.SalesReportDTO;
import com.LaMusic.dto.SalesSummaryDTO;
import com.LaMusic.dto.TopCustomerDTO;
import com.LaMusic.dto.ProductSalesReportDTO;
import com.LaMusic.dto.ProductSalesReportItemDTO;
import com.LaMusic.dto.AdvancedSalesReportDTO;

import com.LaMusic.services.ReportService;


import lombok.AllArgsConstructor;

@CrossOrigin(origins = "*")
@RequestMapping("/reports")
@AllArgsConstructor
@RestController
public class ReportController {

	@Autowired
	private ReportService reportService; 
	
    @GetMapping("/sales")
    public ResponseEntity<AdvancedSalesReportDTO> getSalesReport (
            @RequestParam("start") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate end
            ){
        return ResponseEntity.ok(reportService.generateSalesReport(start, end));
    }

	@GetMapping("/sales-by-product")
    public ResponseEntity<ProductSalesReportDTO> getSalesByProductReport(
            @RequestParam("start") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate end
    ){
        return ResponseEntity.ok(reportService.generateProductSalesReport(start, end));
    }

	
	//Retornar os produtos mais vendidos com base na quantidade vendida, em ordem decrescente, dentro de um intervalo de datas.
    @GetMapping("/best-selling-products")
    public ResponseEntity<BestSellingProductsReportDTO> getBestSellingProducts(
            @RequestParam("start") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate end
            ) {
        return ResponseEntity.ok(reportService.getBestSellingProducts(start, end));
    }
	
	//Retornar produtos com estoque atual menor ou igual a um limite definido (ex: 5 unidades).
	@GetMapping("/low-stock-products")
	public ResponseEntity<List<LowStockProductDTO>> getLowStockProducts(
	    @RequestParam(value = "threshold", defaultValue = "5") Integer threshold) {

	    List<LowStockProductDTO> products = reportService.getLowStockProducts(threshold);
	    return ResponseEntity.ok(products);
	}
	
	//Retornar os clientes com maior volume de compras em um determinado período (ex: mês atual), ordenados por valor total gasto ou número de pedidos.
	@GetMapping("/top-customers")
	public ResponseEntity<List<TopCustomerDTO>> getTopCustomers(
	    @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
	    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

	    List<TopCustomerDTO> customers = reportService.getTopCustomers(start, end);
	    return ResponseEntity.ok(customers);
	}
	
	//Receita total , Numero total de pedidos, Ticket Médio
	@GetMapping("/sales-summary")
	public ResponseEntity<SalesSummaryDTO> getSalesSummary(
		@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
		@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate end) {		
		return ResponseEntity.ok(reportService.getSalesSummaryRaw(start, end));
	}
	
	// comparar o mês atual com o mês anterior, ou dois períodos quaisquer escolhidos pelo gestor.
	@GetMapping("/sales-comparison")
	public ResponseEntity<SalesComparisonDTO> getSalesComparison(
	    @RequestParam("start1") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start1,
	    @RequestParam("end1") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end1,
	    @RequestParam("start2") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start2,
	    @RequestParam("end2") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end2) {

	    return ResponseEntity.ok(reportService.getSalesComparison(start1, end1, start2, end2));
	}
	
	//Exibir a quantidade de vendas ou receita total por categoria em intervalos de tempo (por exemplo, por semana ou por mês), para que o gestor identifique tendências.
	//Diferencial: Ajuda o gestor a identificar onde investir ou promover.
	@GetMapping("/category-trends")
	public ResponseEntity<List<CategoryTrendDTO>> getCategoryTrends(
	    @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
	    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
	    
	    return ResponseEntity.ok(reportService.getCategoryTrends(start, end));
	}
	
	//Essa funcionalidade calcula quais produtos devem ser repostos com base em: Quantidade vendida em um intervalo de tempo, Estoque atual
	//Gatilho: se estoque atual < média de vendas no período → sugerir reposição
	@GetMapping("/reorder-suggestions")
	public ResponseEntity<List<ReorderSuggestionDTO>> getReorderSuggestions(
	    @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
	    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
	    
	    return ResponseEntity.ok(reportService.getReorderSuggestions(start, end));
	}
	
	
	//Um endpoint que retorna o faturamento real dos últimos meses e a projeção para os próximos meses
	//Pode ser usado em gráficos (ex: Chart.js ou Recharts no front-end).
	@GetMapping("/revenue-projection")
	public ResponseEntity<List<MonthlyRevenueProjectionDTO>> getRevenueProjection(
	    @RequestParam(name = "monthsBack", defaultValue = "6") int monthsBack,
	    @RequestParam(name = "monthsAhead", defaultValue = "3") int monthsAhead
	) {
	    return ResponseEntity.ok(reportService.getRevenueProjection(monthsBack, monthsAhead));
	}
	
	// lista usuários que não fazem pedidos há mais de X meses.
	@GetMapping("/clientes-inativos")
	public ResponseEntity<List<InactiveCustomerDTO>> getInactiveCustomers(
	        @RequestParam(defaultValue = "6") int months
	) {
	    return ResponseEntity.ok(reportService.getInactiveCustomers(months));
	}

	//relatório de novos usuários por mês
	@GetMapping("/monthly-users-signups")
	public ResponseEntity<List<MonthlyUserSignupDTO>> getMonthlyUserSignups(
	        @RequestParam int monthsBack) {

	    LocalDate end = LocalDate.now();
	    LocalDate start = end.minusMonths(monthsBack);

	    return ResponseEntity.ok(reportService.getMonthlyUserSignups(start, end));
	}

	
}
