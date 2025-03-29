package com.proyectofinal.clave_compas.util;

public enum ReservationStatus {
    PENDING,
    APPROVED,
    CONFIRMED,
    CANCELLED,
    COMPLETED;
    
    @Override
    public String toString() {
        return this.name();
    }
}
