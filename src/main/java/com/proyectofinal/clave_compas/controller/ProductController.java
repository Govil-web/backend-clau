package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import com.proyectofinal.clave_compas.service.ProductService;
import com.proyectofinal.clave_compas.dto.ProductDTO;

import com.proyectofinal.clave_compas.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productServices;

    @GetMapping
    public ResponseEntity<GlobalResponse> findAll(@RequestParam int page, @RequestParam int pageSize) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(productServices.getPaginateProducts(page, pageSize))
                .build();
        return ResponseEntity.ok(gres);
    }
    
    @PostMapping
    private ResponseEntity<GlobalResponse> saveProduct(@Validated @RequestBody ProductDTO productDto)  {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(productServices.saveProduct(productDto))
                .build();

            return ResponseEntity.ok(gres);

    }

    @GetMapping("/{idProduct}")
    public ResponseEntity<GlobalResponse> getProductById(@PathVariable Integer idProduct) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(productServices.getProductById(idProduct))
                .build();
        return ResponseEntity.ok(gres);

    }

    @GetMapping("/search/category/{idCategory}")
    public ResponseEntity<GlobalResponse> getProductByCategory(@PathVariable Integer idCategory) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(productServices.getProductByCategory(idCategory))
                .build();
        return ResponseEntity.ok(gres);

    }

    @DeleteMapping("/{idProduct}")
    public ResponseEntity<GlobalResponse> deleteProductById(@PathVariable Integer idProduct) {
        productServices.deleteProductById(idProduct);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .build();
        return ResponseEntity.ok(gres);

    }

    @PutMapping("/{productoId}/category/{categoriaId}")
    public ResponseEntity<GlobalResponse> putCategoryToProduct(@PathVariable Integer productoId, @PathVariable Integer categoriaId) {
        productServices.putCategoryToProduct(productoId, categoriaId);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.ACCEPTED.value())
                .message(Constants.MENSAJE_EXITO)
                .response(productServices.getProductById(productoId))
                .build();
        return ResponseEntity.ok(gres);
    }
}
