package com.LaMusic.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InactiveCustomerDTO {
    private UUID userId;
    private String name;
    private String email;
    private LocalDate lastOrderDate;
}