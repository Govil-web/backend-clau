package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.FavoriteEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Integer> {

    /**
     * Encuentra todos los favoritos de un usuario específico.
     *
     * @param user el usuario cuyos favoritos queremos encontrar
     * @return lista de entidades de favoritos pertenecientes al usuario
     */
    List<FavoriteEntity> findByUser(UserEntity user);

    /**
     * Encuentra todos los favoritos de un usuario por su ID.
     *
     * @param userId el ID del usuario
     * @return lista de entidades de favoritos pertenecientes al usuario
     */
    List<FavoriteEntity> findByUserId(Long userId);

    /**
     * Encuentra todos los favoritos relacionados con un producto específico.
     *
     * @param product el producto
     * @return lista de entidades de favoritos para ese producto
     */
    List<FavoriteEntity> findByProduct(ProductEntity product);

    /**
     * Encuentra un favorito específico por usuario y producto.
     * Devuelve un Optional que puede estar vacío si no existe tal favorito.
     *
     * @param user el usuario
     * @param product el producto
     * @return Optional con el favorito si existe, vacío si no
     */
    Optional<FavoriteEntity> findByUserAndProduct(UserEntity user, ProductEntity product);

    /**
     * Comprueba si existe un favorito para un usuario y producto específicos.
     *
     * @param user el usuario
     * @param product el producto
     * @return true si existe, false en caso contrario
     */
    boolean existsByUserAndProduct(UserEntity user, ProductEntity product);

    /**
     * Comprueba si existe un favorito por IDs de usuario y producto.
     *
     * @param userId el ID del usuario
     * @param productId el ID del producto
     * @return true si existe, false en caso contrario
     */
    @Query("SELECT COUNT(f) > 0 FROM FavoriteEntity f WHERE f.user.id = :userId AND f.product.idProduct = :productId")
    boolean existsByUserIdAndProductId(Long userId, Integer productId);
    /**
     * Elimina un favorito por usuario y producto.
     *
     * @param user el usuario
     * @param product el producto
     * @return el número de registros eliminados
     */
    long deleteByUserAndProduct(UserEntity user, ProductEntity product);

    /**
     * Elimina un favorito por IDs de usuario y producto.
     *
     * @param userId el ID del usuario
     * @param productId el ID del producto
     * @return el número de registros eliminados
     */
    @Modifying
    @Query("DELETE FROM FavoriteEntity f WHERE f.user.id = :userId AND f.product.idProduct = :productId")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Integer productId);}