package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface RolRepository extends JpaRepository<RolEntity, Long> {
    RolEntity findByName(String rol);

    @Query("""
        SELECT r FROM RolEntity r 
        JOIN UserRolEntity ur ON ur.role.id = r.id
        WHERE ur.user.id= :userId AND ur.enable = true
    """)
    Optional<List<RolEntity>> findRolesByUserId(@Param("userId") Long userId);

}
