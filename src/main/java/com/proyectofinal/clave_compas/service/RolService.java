package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.RolEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.RolRepository;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.mappers.RolMapper;
import com.proyectofinal.clave_compas.dto.RolDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolEntity findByRolName(String rol) {
        return rolRepository.findByName(rol);
    }

    public List<RolDTO> findByUserId(Long userId) {
        List<RolEntity> rolEntities = rolRepository.findRolesByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("No se encontraron roles para el id "+userId));
        return RolMapper.INSTANCE.toDTOs(rolEntities);
    }

    public RolEntity findById(Long id) {return rolRepository.findById(id)
            .orElseThrow(()->new ResourceNotFoundException("El rol con "+id+ " no existe"));}

    public List<RolDTO> findAll() {
        List<RolEntity> rolEntities = rolRepository.findAll();
        return RolMapper.INSTANCE.toDTOs(rolEntities);
    }


}
