package com.LaMusic.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvancedSalesReportDTO {
    // Totais de vendas finalizadas (pagas, enviadas, etc.)
    private int totalCompletedOrders;
    private BigDecimal totalCompletedRevenue;
    private BigDecimal averageCompletedTicket;

    // Totais de vendas pendentes
    private int totalPendingOrders;
    private BigDecimal totalPendingRevenue;

    // Dados para o gráfico
    private List<DailySalesStatusDTO> chart;
}