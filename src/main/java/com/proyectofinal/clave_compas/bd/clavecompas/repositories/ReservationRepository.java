package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
    @Query("SELECT r FROM ReservationEntity r WHERE r.product.idProduct = :idProduct AND r.status != :status")
    List<ReservationEntity> findByIdProductAndStatusNot(@Param("idProduct") Integer idProduct, @Param("status") String status);
    
    @Query("SELECT r FROM ReservationEntity r WHERE r.product.idProduct = :idProduct")
    List<ReservationEntity> findByIdProduct(@Param("idProduct") Integer idProduct);
}