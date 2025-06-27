package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class CartItemResponseDTO {
	private UUID id;
	private int quantity;
	private BigDecimal price;
	private ProductResponseCartDTO product;
}
