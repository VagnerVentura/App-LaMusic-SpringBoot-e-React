package com.LaMusic.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesPeriodDTO {
	private BigDecimal totalRevenue;
	private Long totalOrders;
	private BigDecimal averageTicket;
}