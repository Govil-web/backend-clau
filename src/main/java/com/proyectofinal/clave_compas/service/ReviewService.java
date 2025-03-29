package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReviewEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ProductRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ReviewRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ReservationRepository;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import com.proyectofinal.clave_compas.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.proyectofinal.clave_compas.util.ReservationStatus;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReviewEntity saveReview(ReviewEntity reviewEntity) {
        // Check if the user has completed a reservation for the product
        boolean hasCompletedReservation = reservationRepository.existsByUserIdAndIdProductAndStatus(
                reviewEntity.getUser().getId(), reviewEntity.getProduct().getIdProduct(), ReservationStatus.COMPLETED);

        if (!hasCompletedReservation) {
            throw new BadRequestException("User has not completed a reservation for this product.");
        }
        
        // Check if the user has already reviewed this product
        boolean hasReviewed = reviewRepository.existsByUserIdAndProductId(
                reviewEntity.getUser().getId(), reviewEntity.getProduct().getIdProduct());
                
        if (hasReviewed) {
            throw new BadRequestException("User has already reviewed this product. Please use the update endpoint instead.");
        }

        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        updateProductRating(savedReview.getProduct().getIdProduct());
        return savedReview;
    }
    
    @Transactional
    public ReviewEntity updateReview(ReviewEntity reviewEntity) {
        // Check if the review exists
        Optional<ReviewEntity> existingReview = reviewRepository.findByUserIdAndProductId(
                reviewEntity.getUser().getId(), reviewEntity.getProduct().getIdProduct());
                
        if (existingReview.isEmpty()) {
            throw new ResourceNotFoundException("Review not found for this user and product.");
        }
        
        // Update the existing review
        ReviewEntity reviewToUpdate = existingReview.get();
        reviewToUpdate.setRating(reviewEntity.getRating());
        reviewToUpdate.setComment(reviewEntity.getComment());
        
        ReviewEntity updatedReview = reviewRepository.save(reviewToUpdate);
        updateProductRating(updatedReview.getProduct().getIdProduct());
        return updatedReview;
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> findAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ReviewEntity> findReviewById(Integer id) {
        return reviewRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Page<ReviewEntity> findReviewsByProduct(Integer productId, Pageable pageable) {
        // Check if product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return reviewRepository.findByProductIdPaginated(productId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ReviewEntity> findReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdPaginated(userId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Optional<ReviewEntity> findByUserIdAndProductId(Long userId, Integer productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public Page<ReviewEntity> findByProductIdPaginated(Integer productId, Pageable pageable) {
        return reviewRepository.findByProductIdPaginated(productId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewEntity> findByUserIdPaginated(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdPaginated(userId, pageable);
    }

    private void updateProductRating(Integer productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProduct_IdProduct(productId);
        double averageRating = reviews.stream()
                                      .mapToInt(ReviewEntity::getRating)
                                      .average()
                                      .orElse(0.0);
        int totalReviews = reviews.size();

        ProductEntity product = productRepository.findById(productId)
                                                 .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setAverageRating(averageRating);
        product.setTotalReviews(totalReviews);
        productRepository.save(product);
    }

    @Transactional
    public void deleteReviewById(Integer id) {
        if (reviewRepository.existsById(id)) {
            ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
            Integer productId = review.getProduct().getIdProduct();
            
            reviewRepository.deleteById(id);
            
            // Update product rating after deletion
            updateProductRating(productId);
        } else {
            throw new ResourceNotFoundException("Review with ID " + id + " does not exist.");
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReviewStatistics(Integer productId) {
        // Check if product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        // Get all reviews for the product
        List<ReviewEntity> reviews = reviewRepository.findByProduct_IdProduct(productId);
        
        // Calculate average rating
        double averageRating = 0.0;
        if (!reviews.isEmpty()) {
            averageRating = reviews.stream()
                    .mapToInt(ReviewEntity::getRating)
                    .average()
                    .orElse(0.0);
        }
        
        // Calculate rating distribution
        Map<Integer, Long> ratingDistribution = reviews.stream()
                .collect(Collectors.groupingBy(
                    ReviewEntity::getRating,
                    Collectors.counting()
                ));
        
        // Prepare the response
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", Math.round(averageRating * 10.0) / 10.0); // Round to 1 decimal place
        stats.put("totalReviews", reviews.size());
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }
}