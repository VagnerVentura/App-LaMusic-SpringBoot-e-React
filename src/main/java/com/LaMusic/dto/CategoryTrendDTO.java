package com.LaMusic.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CategoryTrendDTO {
	private String category;
	private LocalDate data;
	private Long totalSales;	
}
