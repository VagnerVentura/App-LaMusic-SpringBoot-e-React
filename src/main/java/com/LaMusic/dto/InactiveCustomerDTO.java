package com.LaMusic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class InactiveCustomerDTO {
    private UUID userId;
    private String name;
    private String email;
    private LocalDate lastOrderDate;
    private BigDecimal totalSpent;
}