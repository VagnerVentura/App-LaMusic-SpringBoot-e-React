package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesReportDTO {

	private int totalOrders;
	private BigDecimal totalRevenue;
	private BigDecimal averageTicket;
	private List<SalePerDayDTO> chart;
	
}
