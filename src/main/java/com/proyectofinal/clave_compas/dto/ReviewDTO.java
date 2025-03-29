package com.proyectofinal.clave_compas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Integer idReview;

    @NotNull(message = "User ID cannot be null")
    private Long idUser;

    @NotNull(message = "Product ID cannot be null")
    private Integer idProduct;

    @NotNull(message = "User name cannot be null")
    private String userName;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotNull(message = "Review date cannot be null")
    private LocalDateTime reviewDate;

    private String comment; // Optional detailed description
}