package com.LaMusic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalePerDayDTO {
    private LocalDate date;
    private BigDecimal amount;
}