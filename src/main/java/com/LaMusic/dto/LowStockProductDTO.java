package com.LaMusic.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LowStockProductDTO {

	private UUID productId;
	private String name;
	private Integer stock;
	
}
