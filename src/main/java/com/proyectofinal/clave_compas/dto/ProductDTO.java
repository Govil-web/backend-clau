package com.proyectofinal.clave_compas.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        Integer idProduct,
        @NotBlank
        String name,
        @NotBlank
        String brand,
        @NotBlank
        String model,
        @Min(1900)
        @Max(2025)
        Integer year,
        @Min(1)
        Integer stock,
        String description,
        @DecimalMin("0.01")
        BigDecimal price,
        Boolean available,
        @NotNull
        Integer idCategory,
        @NotEmpty(message = "La lista no puede estar vacía")
        @Size(min = 1, max = 6, message = "Debe contener entre 1 y 6 imagenes")
        List<String> imageUrls
) {

}
