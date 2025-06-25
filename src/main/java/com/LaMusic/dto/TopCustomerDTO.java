package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopCustomerDTO {

	private UUID customerId;
	private String name;
	private String email;
	private Long ordersCount;
	private BigDecimal totalSpent;
	
}
