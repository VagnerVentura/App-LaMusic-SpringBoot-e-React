package com.LaMusic.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.dto.BestSellingProductDTO;
import com.LaMusic.dto.LowStockProductDTO;
import com.LaMusic.dto.SalesComparisonDTO;
import com.LaMusic.dto.SalesReportDTO;
import com.LaMusic.dto.SalesSummaryDTO;
import com.LaMusic.dto.TopCustomerDTO;
import com.LaMusic.services.ReportService;

import lombok.AllArgsConstructor;

@RequestMapping("/reports")
@AllArgsConstructor
@RestController
public class ReportController {

	@Autowired
	private ReportService reportService; 
	
	@GetMapping("/sales")
	public ResponseEntity<SalesReportDTO> getSalesReport (
			@RequestParam("start") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate end
			){
		return ResponseEntity.ok(reportService.generateSalesReport(start, end));
	}
	
	//Retornar os produtos mais vendidos com base na quantidade vendida, em ordem decrescente, dentro de um intervalo de datas.
	@GetMapping("/best-selling-products")
	public ResponseEntity<List<BestSellingProductDTO>> getBestSellingProducts(
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
}
