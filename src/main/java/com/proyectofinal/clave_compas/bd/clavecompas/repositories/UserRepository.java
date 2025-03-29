package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.RolEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public Optional<UserEntity> findByEmail(String email);

    @Query("""
        SELECT r.name FROM RolEntity r 
        JOIN UserRolEntity ur ON ur.role.id = r.id
        WHERE ur.user.id= :userId AND ur.enable = true
    """)
    Set<String> findEnabledRolesByUserId(@Param("userId") Long userId);

    Page<UserEntity> findAllByIsAdminNull(Pageable pageable);

    Optional<UserEntity> findById(@Param("userId") Integer userId);


}
