package com.proyectofinal.clave_compas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TokenRefreshDTO (
        @NotBlank( message = "el token no debe estar en blanco")
        @NotNull( message = "el token no debe ser nulo")
        String refreshToken
){

}
