package com.proyectofinal.clave_compas.bd.clavecompas.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "image", schema = "clavecompas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image")
    private Integer idImage;

    @Column(name = "image_url",nullable = false, length = 255)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private ProductEntity product;

}
