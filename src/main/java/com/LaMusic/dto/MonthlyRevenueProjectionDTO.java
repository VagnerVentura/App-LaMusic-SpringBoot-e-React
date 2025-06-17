package com.LaMusic.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyRevenueProjectionDTO {
    private Integer month;
    private BigDecimal revenue;
    private boolean projected;
}