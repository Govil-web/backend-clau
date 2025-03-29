package com.proyectofinal.clave_compas.bd.clavecompas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feature", schema = "clavecompas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feature")
    private Integer idFeature;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column( columnDefinition = "TEXT")
    private String description;

    @Column
    private String iconUrl;

}
