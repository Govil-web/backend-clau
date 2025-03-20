package com.proyectofinal.clave_compas.mappers;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
import com.proyectofinal.clave_compas.dto.ReservationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Named;


@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(source = "product.idProduct", target = "idProduct")
    @Mapping(source = "user.id", target = "idUser")
    public abstract ReservationDTO toDTO(ReservationEntity reservationEntity);

    @InheritInverseConfiguration
    @Mapping(source = "idProduct", target = "product.idProduct")
    @Mapping(target = "user", expression = "java(UserEntity.builder().id(integerToLong(reservationDTO.getIdUser())).build())")
    public abstract ReservationEntity toEntity(ReservationDTO reservationDTO);
    
    default void updateFromDTO(ReservationDTO dto, ReservationEntity entity) {
        if (dto == null || entity == null) return;
        
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entity.setEndDate(dto.getEndDate());
        if (dto.getQuantity() != null) entity.setQuantity(dto.getQuantity());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
    }

    @Named("integerToLong")
    default Long integerToLong(Integer value) {
        return value == null ? null : value.longValue();
    }
}
