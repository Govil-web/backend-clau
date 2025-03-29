package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.FeatureEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.FeatureRepository;
import com.proyectofinal.clave_compas.dto.FeatureDTO;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.mappers.FeatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureService {
    private final FeatureRepository featureRepository;

    public List<FeatureDTO> getAllFeatures(){
        List<FeatureEntity> allFeatures = featureRepository.findAll();
        if(allFeatures==null){
            throw new ResourceNotFoundException("No Feature found");
        }
        
        return FeatureMapper.INSTANCE.toDTOs(allFeatures);
    }

    public FeatureEntity saveFeature(FeatureDTO categoria) {
        FeatureEntity category = FeatureMapper.INSTANCE.toFeatureEntity(categoria);
        featureRepository.save(category);
        return category;
    }

    public Page<FeatureDTO> getPaginateFeatures(int page, int size ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeatureEntity> categories = featureRepository.findAll(pageable);
        return FeatureMapper.INSTANCE.toDTOs(categories);
    }
}
