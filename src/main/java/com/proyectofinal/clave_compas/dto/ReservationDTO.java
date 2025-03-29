package com.proyectofinal.clave_compas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import com.proyectofinal.clave_compas.util.ReservationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Integer idReservation;
    
    @NotNull(message = "Product ID cannot be null")
    @Positive(message = "Product ID must be a positive number")
    private Integer idProduct;
    
    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    private Integer idUser;
    
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;
    
    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;
    
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @NotNull(message = "Product ID cannot be null")
    @Positive(message = "Product ID must be a positive number")
    private Integer productId;
    
    @NotNull(message = "Status cannot be null")
    private ReservationStatus status;
}
