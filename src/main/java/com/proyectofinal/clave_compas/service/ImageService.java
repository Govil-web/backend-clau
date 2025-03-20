package com.proyectofinal.clave_compas.service;


import com.proyectofinal.clave_compas.bd.clavecompas.entities.ImageEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";
    
    private final ImageRepository imageRepository;

    // FIXME: En Lugar de este contructor usamos RequiredArgsConstructor
    /*public ImageServices(@Autowired ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }*/
    @Transactional(transactionManager = "txManagerClavecompas", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class})
    public void saveImages(Integer idProduct, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        ProductEntity productEntity = ProductEntity.builder().idProduct(idProduct).build();
        List<ImageEntity> images = imageUrls.stream()
                .filter(imgUrl -> imgUrl != null && !imgUrl.isEmpty())
                .map(imgUrl -> ImageEntity.builder()
                        .imageUrl(imgUrl)
                        .product(productEntity)
                        .build())
                .collect(Collectors.toList());

        imageRepository.saveAll(images);
    }
}
