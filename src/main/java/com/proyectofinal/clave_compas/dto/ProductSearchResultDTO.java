package com.proyectofinal.clave_compas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchResultDTO {
    private Integer idProduct;
    private String name;
    private String brand;
    private String model;
    private BigDecimal price;
    private String categoryName;
    private String mainImageUrl;
    private Boolean isAvailable;
    private Integer availableStock;
}