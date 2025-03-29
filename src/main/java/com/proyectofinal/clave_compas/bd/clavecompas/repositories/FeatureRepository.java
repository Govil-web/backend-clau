package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.CategoryEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.FeatureEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository extends JpaRepository<FeatureEntity, Integer> {
    List<FeatureEntity> findAll();
    Optional<FeatureEntity> findByIdFeature(Integer idFeature);
    Page<FeatureEntity> findAll(Pageable pageable);
}
