package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReviewEntity;
import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import com.proyectofinal.clave_compas.service.ReviewService;
import com.proyectofinal.clave_compas.util.Constants;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.dto.ReviewDTO;
import com.proyectofinal.clave_compas.exception.BadRequestException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.proyectofinal.clave_compas.security.userdetail.UserDetailIsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proyectofinal.clave_compas.mappers.ReviewMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Operation(summary = "Create a new review", description = "Creates a new review for a product that the user has completed a reservation for")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user has already reviewed this product"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<GlobalResponse> saveReview(@AuthenticationPrincipal UserDetailIsImpl userDetails,
            @RequestBody ReviewDTO reviewDTO) {
        reviewDTO.setIdUser(userDetails.getUserId());
        reviewDTO.setUserName(userDetails.getFirstName());

        ReviewEntity reviewEntity = reviewMapper.toEntity(reviewDTO);
        ReviewEntity savedReview = reviewService.saveReview(reviewEntity);

        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reviewMapper.toDTO(savedReview))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(gres);
    }

    @Operation(summary = "Update an existing review", description = "Updates a user's existing review for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<GlobalResponse> updateReview(@AuthenticationPrincipal UserDetailIsImpl userDetails,
            @RequestBody ReviewDTO reviewDTO) {
        reviewDTO.setIdUser(userDetails.getUserId());

        ReviewEntity reviewEntity = reviewMapper.toEntity(reviewDTO);
        ReviewEntity updatedReview = reviewService.updateReview(reviewEntity);

        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reviewMapper.toDTO(updatedReview))
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Get all reviews", description = "Retrieves all reviews in the system")
    @GetMapping
    public ResponseEntity<GlobalResponse> findAllReviews() {
        List<ReviewEntity> reviews = reviewService.findAllReviews();
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reviews)
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Get review by ID", description = "Retrieves a specific review by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> findReviewById(@PathVariable Integer id) {
        ReviewEntity review = reviewService.findReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(review)
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Get reviews by product", description = "Retrieves all reviews for a specific product with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<GlobalResponse> findReviewsByProduct(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewEntity> reviews = reviewService.findReviewsByProduct(productId, pageable);
        
        Page<ReviewDTO> reviewDTOs = reviews.map(reviewMapper::toDTO);

        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reviewDTOs)
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Get reviews by user", description = "Retrieves all reviews created by a specific user with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<GlobalResponse> findReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewEntity> reviews = reviewService.findReviewsByUser(userId, pageable);
        
        Page<ReviewDTO> reviewDTOs = reviews.map(reviewMapper::toDTO);

        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reviewDTOs)
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Get user's review for a product", description = "Retrieves a specific user's review for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<GlobalResponse> findReviewByUserAndProduct(
            @PathVariable Long userId,
            @PathVariable Integer productId) {

        Optional<ReviewEntity> reviewOpt = reviewService.findByUserIdAndProductId(userId, productId);

        ReviewEntity review = reviewOpt.orElse(null);

        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(review)
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Get current user's reviews", description = "Retrieves all reviews created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-reviews")
    public ResponseEntity<GlobalResponse> findCurrentUserReviews(
            @AuthenticationPrincipal UserDetailIsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewEntity> reviews = reviewService.findReviewsByUser(userDetails.getUserId(), pageable);

        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(reviews)
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Delete a review", description = "Deletes a specific review by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PreAuthorize("hasRole('ADMIN') or @reviewSecurity.isReviewOwner(#id, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteReviewById(@PathVariable Integer id) {
        reviewService.deleteReviewById(id);
        GlobalResponse gres = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .build();
        return ResponseEntity.ok(gres);
    }

    @Operation(summary = "Get review statistics for a product", description = "Returns average rating and total reviews count for a product")
    @GetMapping("/stats/{productId}")
    public ResponseEntity<GlobalResponse> getReviewStats(@PathVariable Integer productId) {
        Map<String, Object> stats = reviewService.getReviewStatistics(productId);

        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(stats)
                .build();

        return ResponseEntity.ok(response);
    }
}