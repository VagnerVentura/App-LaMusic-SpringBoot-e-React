package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesReportDTO {
    private List<ProductSalesReportItemDTO> items;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
}