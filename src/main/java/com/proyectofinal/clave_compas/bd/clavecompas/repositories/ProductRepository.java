package com.proyectofinal.clave_compas.bd.clavecompas.repositories;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    public Page<ProductEntity> findAll(Pageable pageable);
    public Optional<ProductEntity> findByName(String name);
    public Optional<ProductEntity> findById(Integer id);


    // Búsqueda básica por palabra clave (nombre, descripción, marca o modelo)
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.model) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ProductEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Búsqueda avanzada con múltiples criterios
    @Query(value = "SELECT * FROM clavecompas.product p WHERE " +
            "(:keyword IS NULL OR " +
            "   LOWER(p.name::text) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   LOWER(p.description::text) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   LOWER(p.brand::text) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   LOWER(p.model::text) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR p.id_category = :categoryId) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (p.available = true)",
            nativeQuery = true)
    Page<ProductEntity> advancedSearch(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    // Para autocompletado - sugerencias basadas en nombres de productos
    @Query(value = "SELECT DISTINCT p.name FROM clavecompas.product p " +
            "WHERE LOWER(p.name::text) LIKE LOWER(CONCAT(:prefix, '%')) " +
            "ORDER BY p.name LIMIT 10",
            nativeQuery = true)
    List<String> findSuggestionsByPrefix(@Param("prefix") String prefix);

    // Para autocompletado - sugerencias basadas en marcas
    @Query(value = "SELECT DISTINCT p.brand FROM clavecompas.product p " +
            "WHERE p.brand IS NOT NULL AND " +
            "LOWER(p.brand) LIKE LOWER(CONCAT(:prefix, '%')) " +
            "ORDER BY p.brand LIMIT 5",
            nativeQuery = true)
    List<String> findBrandSuggestionsByPrefix(@Param("prefix") String prefix);

    // Para autocompletado - sugerencias basadas en modelos
    @Query(value = "SELECT DISTINCT p.model FROM clavecompas.product p " +
            "WHERE p.model IS NOT NULL AND " +
            "LOWER(p.model) LIKE LOWER(CONCAT(:prefix, '%')) " +
            "ORDER BY p.model LIMIT 5",
            nativeQuery = true)
    List<String> findModelSuggestionsByPrefix(@Param("prefix") String prefix);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.idCategory = :categoryId")
    List<ProductEntity> findByCategory(@Param("categoryId") Integer categoryId);


    @Query(value =
            "SELECT p.* FROM clavecompas.product p WHERE " +
                    "(:keyword IS NULL OR " +
                    "   LOWER(p.name::text) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "   LOWER(p.description::text) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "   LOWER(p.brand::text) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "   LOWER(p.model::text) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "AND (:categoryId IS NULL OR p.id_category = :categoryId) " +
                    "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                    "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
                    "AND (p.available = true) " +
                    "AND (p.stock >= :quantity) " +
                    "AND NOT EXISTS (" +
                    "   SELECT 1 FROM clavecompas.reservation r " +
                    "   WHERE r.id_product = p.id_product " +
                    "   AND r.status != 'CANCELLED' " +
                    "   AND r.enddate >= :startDate " +
                    "   AND r.startdate <= :endDate " +
                    "   AND (" +
                    "       SELECT COALESCE(SUM(r2.quantity), 0) " +
                    "       FROM clavecompas.reservation r2 " +
                    "       WHERE r2.id_product = p.id_product " +
                    "       AND r2.status != 'CANCELLED' " +
                    "       AND r2.enddate >= :startDate " +
                    "       AND r2.startdate <= :endDate" +
                    "   ) + :quantity > p.stock" +
                    ")",
            nativeQuery = true)
    Page<ProductEntity> advancedSearchWithAvailability(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("quantity") Integer quantity,
            Pageable pageable);
}
