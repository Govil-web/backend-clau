package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.dto.FavoriteDTO;
import com.proyectofinal.clave_compas.service.interfaces.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Marca un producto como favorito para el usuario autenticado.
     * Recibe el ID del producto en el cuerpo de la petición.
     *
     * @param requestBody Objeto JSON que contiene el ID del producto
     * @param authentication Objeto de autenticación proporcionado por Spring Security
     * @return DTO con información del favorito creado
     */
    @PostMapping
    public ResponseEntity<FavoriteDTO> addFavorite(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        // Extraemos el ID del producto del cuerpo de la petición
        Integer productId;
        try {
            // Manejo flexible: permite tanto Integer como String en el JSON
            Object rawProductId = requestBody.get("productId");
            if (rawProductId instanceof Integer) {
                productId = (Integer) rawProductId;
            } else if (rawProductId instanceof String) {
                productId = Integer.valueOf((String) rawProductId);
            } else {
                return ResponseEntity
                        .badRequest()
                        .body(null); // O puedes retornar un objeto de error más descriptivo
            }
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body(null); // O puedes retornar un objeto de error más descriptivo
        }

        // Extraemos el nombre de usuario del objeto de autenticación
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Delegamos al servicio para agregar el favorito
        FavoriteDTO favorite = favoriteService.addFavoriteByUsername(username, productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
    }

    /**
     * Elimina un producto de los favoritos del usuario autenticado.
     *
     * @param productId ID del producto a eliminar de favoritos
     * @param authentication Objeto de autenticación proporcionado por Spring Security
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, String>> removeFavorite(
            @PathVariable Integer productId,
            Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        boolean removed = favoriteService.removeFavoriteByUsername(username, productId);

        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Producto eliminado de favoritos"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Producto no encontrado en favoritos"));
        }
    }

    /**
     * Verifica si un producto está en la lista de favoritos del usuario.
     *
     * @param productId ID del producto a verificar
     * @param authentication Objeto de autenticación proporcionado por Spring Security
     * @return Estado del favorito
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @PathVariable Integer productId,
            Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        boolean isFavorite = favoriteService.isFavoriteByUsername(username, productId);

        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    /**
     * Obtiene todos los productos favoritos del usuario autenticado.
     *
     * @param authentication Objeto de autenticación proporcionado por Spring Security
     * @return Lista de favoritos del usuario
     */
    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getUserFavorites(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        List<FavoriteDTO> favorites = favoriteService.getUserFavoritesByUsername(username);

        return ResponseEntity.ok(favorites);
    }
}