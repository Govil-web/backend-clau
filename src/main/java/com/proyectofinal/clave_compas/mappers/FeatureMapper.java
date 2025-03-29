package com.proyectofinal.clave_compas.mappers;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.FeatureEntity;
import com.proyectofinal.clave_compas.dto.FeatureDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface FeatureMapper {

    FeatureMapper INSTANCE = Mappers.getMapper(FeatureMapper.class);

    
    FeatureDTO toFeatureDTO(FeatureEntity categoryEntity);
    FeatureEntity toFeatureEntity(FeatureDTO featureDTO);
    List<FeatureDTO> toDTOs(List<FeatureEntity> categories);

    default Page<FeatureDTO> toDTOs(Page<FeatureEntity> entities) {
        List<FeatureDTO> dtos = entities.stream()
                .map(this::toFeatureDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, entities.getPageable(), entities.getTotalElements());
    }
}
