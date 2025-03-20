package com.proyectofinal.clave_compas.controller.responses;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class GlobalResponse implements Serializable {
    private int statusCode;
    private String message;
    private Object response;
}
