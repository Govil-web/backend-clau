package com.proyectofinal.clave_compas.exception;

public class ProductAlreadyOnRepositoryException extends RuntimeException{
    public ProductAlreadyOnRepositoryException(String message) {
        super(message);
    }
}
