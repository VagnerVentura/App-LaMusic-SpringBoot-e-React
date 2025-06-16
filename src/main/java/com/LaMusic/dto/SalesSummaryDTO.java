package com.LaMusic.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesSummaryDTO {
	private BigDecimal totalRevenue;
	private Long totalOrders;
	private BigDecimal averageTicket;
}
