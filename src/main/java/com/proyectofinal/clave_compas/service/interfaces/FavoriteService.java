package com.proyectofinal.clave_compas.service.interfaces;

import com.proyectofinal.clave_compas.dto.FavoriteDTO;

import java.util.List;

public interface FavoriteService {

    /**
     * Marca un producto como favorito para el usuario actual.
     *
     * @param userId    ID del usuario
     * @param productId ID del producto a marcar como favorito
     * @return          DTO con información del favorito creado
     * @throws          IllegalArgumentException si el usuario o producto no existen
     */
    FavoriteDTO addFavorite(Long userId, Integer productId);

    /**
     * Elimina un producto de los favoritos del usuario.
     *
     * @param userId    ID del usuario
     * @param productId ID del producto a eliminar de favoritos
     * @return          true si fue eliminado, false si no existía
     */
    boolean removeFavorite(Long userId, Integer productId);

    /**
     * Verifica si un producto está marcado como favorito por el usuario.
     *
     * @param userId    ID del usuario
     * @param productId ID del producto a verificar
     * @return          true si está marcado como favorito, false en caso contrario
     */
    boolean isFavorite(Long userId, Integer productId);

    /**
     * Obtiene todos los productos favoritos de un usuario.
     *
     * @param userId    ID del usuario
     * @return          Lista de DTOs con información de los favoritos
     */
    List<FavoriteDTO> getUserFavorites(Long userId);

    FavoriteDTO addFavoriteByUsername(String username, Integer productId);

    boolean removeFavoriteByUsername(String username, Integer productId);

    boolean isFavoriteByUsername(String username, Integer productId);

    List<FavoriteDTO> getUserFavoritesByUsername(String username);
}