package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
import com.proyectofinal.clave_compas.dto.ReservationDTO;
import com.proyectofinal.clave_compas.service.ReservationService;
import com.proyectofinal.clave_compas.util.Constants;
import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<GlobalResponse> createReservation(@Validated @RequestBody ReservationDTO reservationDTO) {
        ReservationEntity reservation = reservationService.createReservation(reservationDTO);
        GlobalResponse gres = GlobalResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message(Constants.MENSAJE_EXITO)
            .response(reservation)
            .build();
        return ResponseEntity.ok(gres);
    }

    @GetMapping("/availability")
    public ResponseEntity<GlobalResponse> checkAvailability(@RequestParam Integer idProduct, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam Integer quantity) {
        boolean isAvailable = reservationService.isProductAvailable(idProduct, startDate, endDate, quantity);
        GlobalResponse gres = GlobalResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message(Constants.MENSAJE_EXITO)
            .response(isAvailable)
            .build();
        return ResponseEntity.ok(gres);
    }
    
    @Operation(summary = "Obtener todas las reservas de un producto", description = "Devuelve todas las reservas activas para un producto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })   
    @GetMapping("/product/{productId}")
    public ResponseEntity<GlobalResponse> getReservationsByProduct(@PathVariable Integer productId) {
        List<Map<String, Object>> reservations = reservationService.getReservationsByProduct(productId);
        GlobalResponse gres = GlobalResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message(Constants.MENSAJE_EXITO)
            .response(reservations)
            .build();
        return ResponseEntity.ok(gres);
    }
}