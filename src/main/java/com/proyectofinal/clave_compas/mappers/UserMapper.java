package com.proyectofinal.clave_compas.mappers;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import com.proyectofinal.clave_compas.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(UserEntity user);

    UserEntity toEntity(UserDTO userDTO);

    default Page<UserDTO> toDTOs(Page<UserEntity> entities) {
        List<UserDTO> dtos = entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, entities.getPageable(), entities.getTotalElements());
    }
}
