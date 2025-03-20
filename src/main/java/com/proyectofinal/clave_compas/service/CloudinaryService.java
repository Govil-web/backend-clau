package com.proyectofinal.clave_compas.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {


    @Autowired
    private Cloudinary cloudinary;


    private String uploadPreset = "clave_compas";

    public Map<String, String> uploadFile(MultipartFile file) {
        try {
            // Generar un nombre único para el archivo
            String publicId = UUID.randomUUID().toString();

            // Subir el archivo a Cloudinary usando upload_preset
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "upload_preset", uploadPreset
                    )
            );

            // Extraer y devolver la información relevante de la respuesta
            return Map.of(
                    "publicId", (String) uploadResult.get("public_id"),
                    "url", (String) uploadResult.get("url"),
                    "secureUrl", (String) uploadResult.get("secure_url"),
                    "format", (String) uploadResult.get("format"),
                    "width", String.valueOf(uploadResult.get("width")),
                    "height", String.valueOf(uploadResult.get("height"))
            );
        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a Cloudinary", e);
        }
    }

    public String deleteFile(String publicId) {
       try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

            return (String) result.get("result");
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar archivo de Cloudinary", e);
        }
    }
}
