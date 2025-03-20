package com.proyectofinal.clave_compas.controller.responses;

import com.proyectofinal.clave_compas.dto.RolDTO;
import com.proyectofinal.clave_compas.dto.UserDTO;

import java.util.List;

public record UserRolResponse(
        UserDTO user,
        List<RolDTO> roles
) {
}
