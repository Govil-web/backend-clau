package com.proyectofinal.clave_compas.mappers;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.CategoryEntity;
import com.proyectofinal.clave_compas.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(source = "idCategory", target = "idCategory")
    @Mapping(source = "name", target = "name")
    CategoryDTO toCategoryDTO(CategoryEntity categoryEntity);

    @Mapping(source = "idCategory", target = "idCategory")
    @Mapping(source = "name", target = "name")
    @Mapping(target = "parentCategory", ignore = true)
    CategoryEntity toCategoryEntity(CategoryDTO categoryDTO);

    List<CategoryDTO> toDTOs(List<CategoryEntity> categories);
}
