package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseCartDTO {
	
	private UUID id;
    private String name;
    private BigDecimal price;
    private List<ProductImageResponseDTO> images;
	
}
