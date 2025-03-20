package com.proyectofinal.clave_compas.mappers;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.RolEntity;
import com.proyectofinal.clave_compas.dto.RolDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RolMapper {
    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);

    RolDTO toDTO(RolEntity rolEntity);

    RolEntity toEntity(RolDTO rolDTO);

    List<RolDTO> toDTOs(List<RolEntity> roles);

    List<RolEntity> toEntities(List<RolDTO> rolesDTO);
}
