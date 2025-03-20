package com.proyectofinal.clave_compas.exception;

public class AvailabilityException extends RuntimeException {
    
    public AvailabilityException(String message) {
        super(message);
    }

    public AvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvailabilityException() {
        super("Resource availability conflict occurred");
    }
}
