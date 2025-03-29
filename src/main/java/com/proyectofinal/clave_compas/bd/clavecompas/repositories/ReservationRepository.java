package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
import com.proyectofinal.clave_compas.util.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
    @Query("SELECT r FROM ReservationEntity r WHERE r.product.idProduct = :idProduct AND r.status != :status")
    List<ReservationEntity> findByIdProductAndStatusNot(@Param("idProduct") Integer idProduct, @Param("status") ReservationStatus status);
    
    @Query("SELECT r FROM ReservationEntity r WHERE r.product.idProduct = :idProduct")
    List<ReservationEntity> findByIdProduct(@Param("idProduct") Integer idProduct);
    
    @Query("SELECT COUNT(r) > 0 FROM ReservationEntity r WHERE r.user.id = :userId AND r.product.idProduct = :idProduct AND r.status = :status")
    boolean existsByUserIdAndIdProductAndStatus(@Param("userId") Long userId, @Param("idProduct") Integer productId, @Param("status") ReservationStatus status);
    
    @Query("SELECT r FROM ReservationEntity r WHERE r.user.id = :userId AND r.product.idProduct = :idProduct AND r.status = :status")
    List<ReservationEntity> findByUserIdAndIdProductAndStatus(@Param("userId") Long userId, @Param("idProduct") Integer idProduct, @Param("status") ReservationStatus status);
    
    @Query("SELECT r FROM ReservationEntity r WHERE r.user.id = :userId AND r.status = :status")
    List<ReservationEntity> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ReservationStatus status);
}