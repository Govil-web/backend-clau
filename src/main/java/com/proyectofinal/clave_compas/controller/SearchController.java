package com.proyectofinal.clave_compas.controller;

import com.proyectofinal.clave_compas.dto.ProductSearchResultDTO;
import com.proyectofinal.clave_compas.dto.SearchRequestDTO;
import com.proyectofinal.clave_compas.service.SearchService;
import com.proyectofinal.clave_compas.util.Constants;
import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "Buscar productos", description = "Busca productos según varios criterios, incluida la disponibilidad en fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    @GetMapping("/products")
    public ResponseEntity<GlobalResponse> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "8") Integer size) {

        SearchRequestDTO searchRequest = SearchRequestDTO.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .startDate(startDate)
                .endDate(endDate)
                .quantity(quantity != null ? quantity : 1)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();

        Page<ProductSearchResultDTO> results = searchService.searchProducts(searchRequest);

        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(results)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener sugerencias de autocompletado", description = "Devuelve sugerencias para autocompletar basadas en el texto de búsqueda")
    @GetMapping("/autocomplete")
    public ResponseEntity<GlobalResponse> getAutocompleteSuggestions(
            @RequestParam String query) {
        List<String> suggestions = searchService.getAutocompleteSuggestions(query);

        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Constants.MENSAJE_EXITO)
                .response(suggestions)
                .build();

        return ResponseEntity.ok(response);
    }
}