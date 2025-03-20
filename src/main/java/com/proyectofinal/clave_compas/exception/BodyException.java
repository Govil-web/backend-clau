package com.proyectofinal.clave_compas.exception;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class BodyException {
    private int statusCode;
    private String message;
    private Map<String, String> NotValidParams;
}
