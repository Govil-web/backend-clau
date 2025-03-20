package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.FavoriteEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.FavoriteRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ProductRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.UserRepository;
import com.proyectofinal.clave_compas.dto.FavoriteDTO;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.mappers.FavoriteMapper;
import com.proyectofinal.clave_compas.service.interfaces.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;




@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FavoriteMapper favoriteMapper;

    @Autowired
    public FavoriteServiceImpl(
            FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    @Transactional
    public FavoriteDTO addFavorite(Long userId, Integer productId) {
        // Verificar si el usuario existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        // Verificar si el producto existe
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        // Verificar si ya está marcado como favorito
        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            // Ya está marcado como favorito, retornar el existente
            FavoriteEntity existingFavorite = favoriteRepository.findByUserAndProduct(user, product)
                    .orElseThrow(); // No debería ocurrir debido a la verificación anterior

            return favoriteMapper.toDTO(existingFavorite);
        }

        // Crear nueva entidad de favorito
        FavoriteEntity favorite = new FavoriteEntity(user, product);

        try {
            // Guardar en la base de datos
            FavoriteEntity savedFavorite = favoriteRepository.save(favorite);

            // Mapear a DTO y retornar
            return favoriteMapper.toDTO(savedFavorite);
        } catch (DataIntegrityViolationException e) {
            // Manejar el caso de concurrencia (otro hilo podría haber creado el mismo favorito)
            throw new IllegalStateException("No se pudo agregar el favorito debido a un conflicto de datos", e);
        }
    }

    @Override
    @Transactional
    public boolean removeFavorite(Long userId, Integer productId) {
        // Verificar si existe el favorito
        if (!favoriteRepository.existsByUserIdAndProductId(userId,productId)) {
            return false; // No existía, no hay nada que eliminar
        }

        // Eliminar el favorito
        long deletedCount = favoriteRepository.deleteByUserIdAndProductId(userId, productId);

        // Retornar true si se eliminó algo
        return deletedCount > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Integer productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getUserFavorites(Long userId) {
        // Verificar si el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }

        // Obtener todos los favoritos del usuario
        List<FavoriteEntity> favorites = favoriteRepository.findByUserId(userId);

        // Mapear a DTOs usando el mapper
        return favoriteMapper.toDTOList(favorites);
    }

    @Override
    @Transactional
    public FavoriteDTO addFavoriteByUsername(String username, Integer productId) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return addFavorite(user.getId(), productId);
    }

    @Override
    @Transactional
    public boolean removeFavoriteByUsername(String username, Integer productId) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return removeFavorite(user.getId(), productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavoriteByUsername(String username, Integer productId) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return isFavorite(user.getId(), productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getUserFavoritesByUsername(String username) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return getUserFavorites(user.getId());
}

}