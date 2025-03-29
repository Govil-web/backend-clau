package com.proyectofinal.clave_compas.security;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReviewEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ReviewRepository;
import com.proyectofinal.clave_compas.security.userdetail.UserDetailIsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;

    public boolean isReviewOwner(Integer reviewId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        UserDetailIsImpl userDetails = (UserDetailIsImpl) authentication.getPrincipal();
        Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isEmpty()) {
            return false;
        }

        ReviewEntity review = reviewOpt.get();
        return review.getUser().getId().equals(userDetails.getUserId());
    }
}