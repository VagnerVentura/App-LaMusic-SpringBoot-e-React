package com.LaMusic.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private String slug;
    private BigDecimal price;
    private String imageUrl;
}