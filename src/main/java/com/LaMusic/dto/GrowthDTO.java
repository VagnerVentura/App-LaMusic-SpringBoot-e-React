package com.LaMusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrowthDTO {
	private Double revenueGrowth;
	private Double ordersGrowth;
	private Double ticketGrowth;
}
