package com.LaMusic.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BestSellingProductDTO {

	private UUID productId;
	private String name;
	private Long quantitySold;
	private BigDecimal totalRevenue;
	
}
