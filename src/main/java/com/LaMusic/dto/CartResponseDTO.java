package com.LaMusic.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
	
    private UUID id;
    private String status;
    private List<CartItemResponseDTO> items;
	
}
