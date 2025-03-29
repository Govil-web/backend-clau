package com.proyectofinal.clave_compas.mappers;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.CategoryEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import com.proyectofinal.clave_compas.dto.CategoryDTO;
import com.proyectofinal.clave_compas.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(source = "idCategory", target = "idCategory")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "imageUrl" ,target = "imageUrl")
    CategoryDTO toCategoryDTO(CategoryEntity categoryEntity);

    @Mapping(source = "idCategory", target = "idCategory")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "imageUrl" ,target = "imageUrl")
    @Mapping(target = "parentCategory", ignore = true)
    CategoryEntity toCategoryEntity(CategoryDTO categoryDTO);

    List<CategoryDTO> toDTOs(List<CategoryEntity> categories);

    default Page<CategoryDTO> toDTOs(Page<CategoryEntity> entities) {
        List<CategoryDTO> dtos = entities.stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, entities.getPageable(), entities.getTotalElements());
    }
}
