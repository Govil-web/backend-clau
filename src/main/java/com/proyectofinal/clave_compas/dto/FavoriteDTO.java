package com.proyectofinal.clave_compas.dto;

import java.time.LocalDateTime;

public class FavoriteDTO {
    private Integer id;
    private Long userId;
    private Integer productId;
    private String productName;
    private String productImage;
    private LocalDateTime createdAt;

    // Constructor vacío
    public FavoriteDTO() {
    }

    // Constructor con parámetros
    public FavoriteDTO(Integer id, Long userId, Integer productId, String productName,
                       String productImage, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.createdAt = createdAt;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}