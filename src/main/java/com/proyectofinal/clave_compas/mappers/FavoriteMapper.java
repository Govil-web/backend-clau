package com.proyectofinal.clave_compas.mappers;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.FavoriteEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ImageEntity;
import com.proyectofinal.clave_compas.dto.FavoriteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    FavoriteMapper INSTANCE = Mappers.getMapper(FavoriteMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.idProduct", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.images", target = "productImage", qualifiedByName = "getFirstImageUrl")
    @Mapping(source = "product.price", target = "price")
    FavoriteDTO toDTO(FavoriteEntity entity);

    List<FavoriteDTO> toDTOList(List<FavoriteEntity> entities);

    @org.mapstruct.Named("getFirstImageUrl")
    default String getFirstImageUrl(List<ImageEntity> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.get(0).getImageUrl();
    }
}
