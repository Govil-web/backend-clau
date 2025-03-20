package com.proyectofinal.clave_compas.controller;


import com.proyectofinal.clave_compas.controller.responses.CategoryResponse;
import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import com.proyectofinal.clave_compas.service.CategoryService;
import com.proyectofinal.clave_compas.dto.CategoryDTO;
import com.proyectofinal.clave_compas.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value ="categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<GlobalResponse> findAll() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        CategoryResponse categoryResponse = new CategoryResponse(categories);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(categoryResponse)
                .build();

        return ResponseEntity.ok(gres);
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> saveCategory(@RequestBody CategoryDTO category) {
        categoryService.saveCategory(category);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(Constants.MENSAJE_EXITO)
                .build();
        return ResponseEntity.ok(gres);
    }
}
