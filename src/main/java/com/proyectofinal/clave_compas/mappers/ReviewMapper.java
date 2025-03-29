package com.proyectofinal.clave_compas.mappers;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReviewEntity;
import com.proyectofinal.clave_compas.dto.ReviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "idUser", target = "user.id")
    @Mapping(source = "idProduct", target = "product.idProduct")
    @Mapping(source = "userName", target = "userName")
    ReviewEntity toEntity(ReviewDTO reviewDTO);

    @Mapping(source = "user.id", target = "idUser")
    @Mapping(source = "product.idProduct", target = "idProduct")
    @Mapping(source = "userName", target = "userName")
    ReviewDTO toDTO(ReviewEntity reviewEntity);
}