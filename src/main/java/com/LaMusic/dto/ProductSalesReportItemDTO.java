package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesReportItemDTO {
    private UUID productId;
    private String productName;
    private Long quantitySold;
    private BigDecimal totalRevenue;
}