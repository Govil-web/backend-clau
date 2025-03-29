package com.proyectofinal.clave_compas.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {

    public static final String AUTH_LOGIN_URL = "/auth";

    // Signing key for HS512 algorithm
    // You can use the page http://www.allkeysgenerator.com/ to generate all kinds of keys
    public static final String JWT_SECRET = "TOKEN_SECRET";

    //ESQUEMA DE BASE DE DATOS SEGURIDAD
    public static final String ESQUEMA_SEGURIDAD = "seguridad";

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_ADMIN = "TOKEN_ADMIN";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";

    // CONFIGURACIÓN DE AUTENTICACIÓN
    public static final String LOCALIZACION="Zona";
    public static final String HEAD_USERNAME="username";
    public static final String HEAD_PASSWORD="password";
    public static final String HEAD_COD_CLIENTE="codigoCliente";
    public static final String HEAD_COD_ROL="codigoRol";
}