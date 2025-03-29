package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
    
    // Change this method to match the actual property name in ProductEntity
    List<ReviewEntity> findByProduct_IdProduct(Integer productId);
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.product.idProduct = :productId")
    Page<ReviewEntity> findByProductIdPaginated(@Param("productId") Integer productId, Pageable pageable);
    
    // Rest of the methods remain unchanged
    @Query("SELECT r FROM ReviewEntity r WHERE r.user.id = :userId")
    Page<ReviewEntity> findByUserIdPaginated(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(r) > 0 FROM ReviewEntity r WHERE r.user.id = :userId AND r.product.idProduct = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Integer productId);
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.user.id = :userId AND r.product.idProduct = :productId")
    Optional<ReviewEntity> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Integer productId);
}