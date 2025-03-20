package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import com.proyectofinal.clave_compas.controller.responses.UserRolResponse;
import com.proyectofinal.clave_compas.dto.TokenRefreshDTO;
import com.proyectofinal.clave_compas.service.RolService;
import com.proyectofinal.clave_compas.service.UserService;
import com.proyectofinal.clave_compas.dto.LoginDTO;
import com.proyectofinal.clave_compas.dto.RolDTO;
import com.proyectofinal.clave_compas.dto.UserDTO;
import com.proyectofinal.clave_compas.util.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RolService rolService;

    @PostMapping(value = "/register")
    public ResponseEntity<GlobalResponse> saveUser(@Validated @RequestBody UserDTO userDTO) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(userService.saveUser(userDTO))
                .build();
        return ResponseEntity.ok(gres);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<GlobalResponse> loginUser(@Validated @RequestBody LoginDTO loginDTO) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(userService.loginUser(loginDTO))
                .build();
        return ResponseEntity.ok(gres);
    }

    @PostMapping(value = "/auth/refresh")
    public ResponseEntity<GlobalResponse> refreshToken(@Validated @RequestBody TokenRefreshDTO tokenRefreshDTO) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(userService.refreshToken(tokenRefreshDTO.refreshToken()))
                .build();
        return ResponseEntity.ok(gres);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> findAll(@RequestParam int page, @RequestParam int pageSize) {
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(userService.getPaginateUsers(page, pageSize))
                .build();
        return ResponseEntity.ok(gres);
    }

    @GetMapping(value = "/{idUser}")
    public ResponseEntity<GlobalResponse> findUserById(@PathVariable Long idUser) {
        UserDTO userDTO = userService.getById(idUser);
        List<RolDTO> roles = rolService.findByUserId(idUser);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(new UserRolResponse(userDTO,roles))
                .build();
        return ResponseEntity.ok(gres);
    }
}
