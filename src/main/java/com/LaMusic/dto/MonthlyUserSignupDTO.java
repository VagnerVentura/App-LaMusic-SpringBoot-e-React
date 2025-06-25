package com.LaMusic.dto;

import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyUserSignupDTO {
    private YearMonth month;
    private Long userCount;
}