package com.proyectofinal.clave_compas.dto;

import jakarta.validation.constraints.*;

public record UserDTO(
        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
        String lastName,

        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "\\d{8,}", message = "El documento debe tener al menos 8 dígitos")
        String document,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
        String phone,

        @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
        String address,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debe ser un correo válido")
        @Size(max = 100, message = "El correo no puede tener más de 100 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        String locality

) {
}
