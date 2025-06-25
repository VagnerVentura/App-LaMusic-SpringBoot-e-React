package com.LaMusic.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BestSellingProductsReportDTO {
    private List<BestSellingProductDTO> completed;
    private List<BestSellingProductDTO> pending;
}