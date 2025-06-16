package com.LaMusic.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SalesSummaryRawDTO {

	private BigDecimal totalRevenue;
	private Long totalOrders;
		
}
