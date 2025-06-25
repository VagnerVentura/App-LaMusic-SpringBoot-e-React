package com.LaMusic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DailySalesStatusDTO {
    private LocalDate date;
    private BigDecimal completedAmount;
    private BigDecimal pendingAmount;

    public DailySalesStatusDTO(LocalDate date) {
        this.date = date;
        this.completedAmount = BigDecimal.ZERO;
        this.pendingAmount = BigDecimal.ZERO;
    }
}