package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.CategoryEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.CategoryRepository;
import com.proyectofinal.clave_compas.dto.UserDTO;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.mappers.CategoryMapper;
import com.proyectofinal.clave_compas.dto.CategoryDTO;
import com.proyectofinal.clave_compas.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories(){
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        if(allCategories==null){
            throw new ResourceNotFoundException("No Category found");
        }
        List<CategoryEntity> subCategories = allCategories.stream().filter(categoryEntity -> categoryEntity.getParentCategory()!=null).toList();
        return CategoryMapper.INSTANCE.toDTOs(subCategories);
    }

    public CategoryEntity saveCategory(CategoryDTO categoria) {
        CategoryEntity category = CategoryMapper.INSTANCE.toCategoryEntity(categoria);
        category.setParentCategory(category);
        categoryRepository.save(category);
        return category;
    }

    public Page<CategoryDTO> getPaginateCategories(int page, int size ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryEntity> categories = categoryRepository.findAll(pageable);
        return CategoryMapper.INSTANCE.toDTOs(categories);
    }
}
