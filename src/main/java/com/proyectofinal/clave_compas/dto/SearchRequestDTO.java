package com.proyectofinal.clave_compas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequestDTO {
    private String keyword;
    private Integer categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer quantity;
    private String sortBy;  // "price", "name"
    private String sortDirection; // "asc", "desc"
    private Integer page;
    private Integer size;
}