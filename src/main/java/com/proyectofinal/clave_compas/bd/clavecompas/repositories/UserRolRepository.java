package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserRolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserRolRepository extends JpaRepository<UserRolEntity, Long> {
    Set<UserRolEntity> findByUserId(Long userId);
}
