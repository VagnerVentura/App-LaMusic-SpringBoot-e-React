package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDTO(
    UUID productId,
    String productName,
    Integer quantity,
    BigDecimal price,
    String imageUrl
) {}
