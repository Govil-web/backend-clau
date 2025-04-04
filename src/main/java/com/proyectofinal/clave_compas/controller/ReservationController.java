package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
import com.proyectofinal.clave_compas.dto.ReservationDTO;
import com.proyectofinal.clave_compas.service.ReservationService;
import com.proyectofinal.clave_compas.util.Constants;
import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import com.proyectofinal.clave_compas.security.userdetail.UserDetailIsImpl;
import com.proyectofinal.clave_compas.util.ReservationStatus;
import com.proyectofinal.clave_compas.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "Check if user has completed reservations for a product", 
               description = "Returns completed reservations for the authenticated user for a specific product")
    @GetMapping("/user/completed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GlobalResponse> getUserCompletedReservations(
            @RequestParam(required = false) Integer productId,
            @AuthenticationPrincipal UserDetailIsImpl userDetails) {
        
        List<Map<String, Object>> completedReservations = 
            reservationService.getUserCompletedReservations(userDetails.getUserId(), productId);
        
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(completedReservations)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update reservation status", 
               description = "Updates the status of an existing reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @PutMapping("/{reservationId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GlobalResponse> updateReservationStatus(
            @PathVariable Integer reservationId,
            @RequestParam ReservationStatus status,
            @AuthenticationPrincipal UserDetailIsImpl userDetails) {
        
        ReservationEntity updatedReservation = 
            reservationService.updateReservationStatus(reservationId, status, userDetails.getUserId());
        
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(updatedReservation)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get reservations by status", 
               description = "Returns all reservations with the specified status for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @GetMapping("/by-status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GlobalResponse> getReservationsByStatus(
            @PathVariable String status,
            @AuthenticationPrincipal UserDetailIsImpl userDetails) {
        
        ReservationStatus reservationStatus;
        try {
            reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid reservation status: " + status);
        }
        
        List<Map<String, Object>> reservations = 
            reservationService.getUserReservationsByStatus(userDetails.getUserId(), reservationStatus);
        
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reservations)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Cancel a reservation", 
               description = "Cancels an existing reservation if it belongs to the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "User not authorized to cancel this reservation")
    })
    @PutMapping("/{reservationId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GlobalResponse> cancelReservation(
            @PathVariable Integer reservationId,
            @AuthenticationPrincipal UserDetailIsImpl userDetails) {
        
        ReservationEntity cancelledReservation = 
            reservationService.updateReservationStatus(reservationId, ReservationStatus.CANCELLED, userDetails.getUserId());
        
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Reservation cancelled successfully")
                .response(cancelledReservation)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get details reservations by user",
               description = "Returns list of reservations with details for the authenticated user")
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GlobalResponse> getUserReservations(@AuthenticationPrincipal UserDetailIsImpl userDetails) {

        List<ReservationDTO> reservations = reservationService.getReservationsByUser(userDetails.getUserId());

        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reservations)
                .build();

        return ResponseEntity.ok(response);
    }
}