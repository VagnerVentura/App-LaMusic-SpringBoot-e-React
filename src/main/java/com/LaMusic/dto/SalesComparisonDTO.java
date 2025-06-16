package com.LaMusic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesComparisonDTO {
	private SalesPeriodDTO period1;
	private SalesPeriodDTO period2;
	private GrowthDTO comparison;
}
