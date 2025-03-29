package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
import com.proyectofinal.clave_compas.util.ReservationStatus;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ReservationRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ProductRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.UserRepository;
import com.proyectofinal.clave_compas.dto.UserDTO;
import com.proyectofinal.clave_compas.mappers.UserMapper;
import com.proyectofinal.clave_compas.dto.ReservationDTO;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.exception.AvailabilityException;
import com.proyectofinal.clave_compas.mappers.ReservationMapper;
import com.proyectofinal.clave_compas.exception.BadRequestException;
import com.proyectofinal.clave_compas.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ReservationMapper reservationMapper;

    @Transactional(readOnly = true)
    public boolean isProductAvailable(Integer idProduct, LocalDate startDate, LocalDate endDate, Integer quantity) {
        ProductEntity product = productRepository.findById(idProduct)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<ReservationEntity> reservations = reservationRepository.findByIdProductAndStatusNot(idProduct, ReservationStatus.CANCELLED);

        int reservedQuantity = reservations.stream()
            .filter(reservation -> !reservation.getEndDate().isBefore(startDate) && !reservation.getStartDate().isAfter(endDate))
            .mapToInt(ReservationEntity::getQuantity)
            .sum();

        return product.getStock() - reservedQuantity >= quantity;
    }

    @Transactional
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
        ReservationEntity savedReservation = reservationRepository.save(reservation);
        ProductEntity product = productRepository.findById(reservationDTO.getIdProduct())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        reservation.setProduct(product);
        
        // Send confirmation email
        UserEntity user = userRepository.findById(reservationDTO.getIdUser())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserDTO userDTO = UserMapper.INSTANCE.toDTO(user);
        emailService.sendReservationConfirmationEmail(userDTO, savedReservation);
        
        return savedReservation;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getReservationsByProduct(Integer productId) {
        // Check if product exists
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
        // Find all active reservations for the given product
        List<ReservationEntity> reservations = reservationRepository.findByIdProductAndStatusNot(productId, ReservationStatus.CANCELLED);
        
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

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserCompletedReservations(Long userId, Integer productId) {
        List<ReservationEntity> reservations;
        
        if (productId != null) {
            reservations = reservationRepository.findByUserIdAndIdProductAndStatus(userId, productId, ReservationStatus.COMPLETED);
        } else {
            reservations = reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.COMPLETED);
        }
        
        return reservations.stream()
            .map(reservation -> {
                Map<String, Object> reservationMap = new HashMap<>();
                reservationMap.put("idReservation", reservation.getIdReservation());
                reservationMap.put("idProduct", reservation.getProduct().getIdProduct());
                reservationMap.put("startDate", reservation.getStartDate());
                reservationMap.put("endDate", reservation.getEndDate());
                reservationMap.put("status", reservation.getStatus());
    
                ProductEntity product = productRepository.findById(reservation.getProduct().getIdProduct())
                    .orElse(null);
                if (product != null) {
                    reservationMap.put("productName", product.getName());
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        reservationMap.put("productImage", product.getImages().get(0));
                    } else {
                        reservationMap.put("productImage", null);
                    }
                }
                
                return reservationMap;
            })
            .collect(Collectors.toList());
    }

    // Add these methods to your ReservationService class
    
    @Transactional
    public ReservationEntity updateReservationStatus(Integer reservationId, ReservationStatus status, Long userId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
        
        // Check if the reservation belongs to the user
        if (!reservation.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to update this reservation");
        }
        
        // Additional validation logic could be added here
        // For example, prevent changing from CANCELLED to another status
        if (reservation.getStatus() == ReservationStatus.CANCELLED && status != ReservationStatus.CANCELLED) {
            throw new BadRequestException("Cannot change status of a cancelled reservation");
        }
        
        // If changing to CANCELLED, check if it's not too late to cancel
        if (status == ReservationStatus.CANCELLED && reservation.getStartDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new BadRequestException("Cannot cancel a reservation less than 24 hours before start date");
        }
        
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserReservationsByStatus(Long userId, ReservationStatus status) {
        List<ReservationEntity> reservations = reservationRepository.findByUserIdAndStatus(userId, status);
        
        return reservations.stream()
            .map(reservation -> {
                Map<String, Object> reservationMap = new HashMap<>();
                reservationMap.put("idReservation", reservation.getIdReservation());
                reservationMap.put("idProduct", reservation.getProduct().getIdProduct());
                reservationMap.put("startDate", reservation.getStartDate());
                reservationMap.put("endDate", reservation.getEndDate());
                reservationMap.put("quantity", reservation.getQuantity());
                reservationMap.put("status", reservation.getStatus());
    
                ProductEntity product = productRepository.findById(reservation.getProduct().getIdProduct())
                    .orElse(null);
                if (product != null) {
                    reservationMap.put("productName", product.getName());
                    reservationMap.put("productImage", product.getImages().isEmpty() ? null : product.getImages().get(0));
                }
                
                return reservationMap;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByUser(Long userId) {
        List<ReservationEntity> reservations = reservationRepository.findByUserId(userId);

        return reservations.stream()
                .map(reservationMapper::toDTO) // Mapear cada entidad a DTO
                .collect(Collectors.toList());
    }
}