package com.proyectofinal.clave_compas.controller;


import com.proyectofinal.clave_compas.bd.clavecompas.repositories.FeatureRepository;
import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import com.proyectofinal.clave_compas.dto.FeatureDTO;
import com.proyectofinal.clave_compas.service.FeatureService;
import com.proyectofinal.clave_compas.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequiredArgsConstructor
@RequestMapping(value ="features")
public class FeatureController {
    private final FeatureService featureService;
    private final FeatureRepository featureRepository;

   

    @PostMapping
    public ResponseEntity<GlobalResponse> saveFeature(@RequestBody FeatureDTO category) {
        featureService.saveFeature(category);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(Constants.MENSAJE_EXITO)
                .build();
        return ResponseEntity.ok(gres);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteFeature( @PathVariable Integer id) {
        if (!featureRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
        }
        featureRepository.deleteById(id);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(Constants.MENSAJE_EXITO)
                .build();
        return ResponseEntity.ok(gres);
    }

    @GetMapping(value = "admin")
    public ResponseEntity<GlobalResponse> findAll(@RequestParam int page, @RequestParam int pageSize) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(featureService.getPaginateFeatures(page, pageSize))
                .build();
        return ResponseEntity.ok(gres);
    }

}
