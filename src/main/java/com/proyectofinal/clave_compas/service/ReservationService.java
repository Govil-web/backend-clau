package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ReservationRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ProductRepository;
import com.proyectofinal.clave_compas.dto.ReservationDTO;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.exception.AvailabilityException;
import com.proyectofinal.clave_compas.mappers.ReservationMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    public boolean isProductAvailable(Integer idProduct, LocalDate startDate, LocalDate endDate, Integer quantity) {
        ProductEntity product = productRepository.findById(idProduct)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<ReservationEntity> reservations = reservationRepository.findByIdProductAndStatusNot(idProduct, "CANCELLED");

        int reservedQuantity = reservations.stream()
            .filter(reservation -> !reservation.getEndDate().isBefore(startDate) && !reservation.getStartDate().isAfter(endDate))
            .mapToInt(ReservationEntity::getQuantity)
            .sum();

        return product.getStock() - reservedQuantity >= quantity;
    }

    public ReservationEntity createReservation(ReservationDTO reservationDTO) {
        // Validar que la fecha de inicio no sea anterior a la fecha actual
        if (reservationDTO.getStartDate().isBefore(LocalDate.now())) {
            throw new AvailabilityException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        
        // Validar que la fecha de fin no sea anterior a la fecha de inicio
        if (reservationDTO.getEndDate().isBefore(reservationDTO.getStartDate())) {
            throw new AvailabilityException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        
        if (!isProductAvailable(reservationDTO.getIdProduct(), reservationDTO.getStartDate(), reservationDTO.getEndDate(), reservationDTO.getQuantity())) {
            throw new AvailabilityException("Product is not available for the selected dates");
        }

        ReservationEntity reservation = ReservationMapper.INSTANCE.toEntity(reservationDTO);
        return reservationRepository.save(reservation);
    }

    /**
     * Get all reservations for a specific product
     * @param productId the ID of the product
     * @return a list of maps containing startDate, endDate, and quantity for each reservation
     */
    public List<Map<String, Object>> getReservationsByProduct(Integer productId) {
        // Check if product exists
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
        // Find all active reservations for the given product
        List<ReservationEntity> reservations = reservationRepository.findByIdProductAndStatusNot(productId, "CANCELLED");
        
        // Transform the entities to the required format
        return reservations.stream()
            .map(reservation -> {
                Map<String, Object> reservationMap = new java.util.HashMap<>();
                reservationMap.put("startDate", reservation.getStartDate());
                reservationMap.put("endDate", reservation.getEndDate());
                reservationMap.put("quantity", reservation.getQuantity());
                reservationMap.put("idReservation", reservation.getIdReservation());
                reservationMap.put("productName", product.getName()); // Asumiendo que Product tiene un campo name
                reservationMap.put("status", reservation.getStatus());
                return reservationMap;
            })
            .collect(java.util.stream.Collectors.toList());
    }
}