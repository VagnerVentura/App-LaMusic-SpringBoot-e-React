package com.LaMusic.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReorderSuggestionDTO {
    private UUID productId;
    private String productName;
    private int currentStock;
    private long quantitySold;
    private long suggestedReorderAmount;
}