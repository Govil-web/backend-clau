package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.controller.requests.UserRolRequest;
import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import com.proyectofinal.clave_compas.service.RolService;
import com.proyectofinal.clave_compas.service.UserRolService;
import com.proyectofinal.clave_compas.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "roluser")
public class UserRolController {
    private final UserRolService userRolService;
    private final RolService rolService;

    @PutMapping(value = "/assignRoles")
    public ResponseEntity<GlobalResponse> assignRoles(@Validated @RequestBody UserRolRequest userRolRequest) {
        GlobalResponse gre = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(userRolService.assignRoles(userRolRequest))
                .build();
        return new ResponseEntity<>(gre, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> getRoles(){
        GlobalResponse gre = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(rolService.findAll())
                .build();
        return new ResponseEntity<>(gre, HttpStatus.OK);
    }
}
