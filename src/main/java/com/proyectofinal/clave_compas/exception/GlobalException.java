package com.proyectofinal.clave_compas.exception;

import com.proyectofinal.clave_compas.controller.responses.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GlobalResponse> tratamientoRNFE(ResourceNotFoundException rnfe){
        GlobalResponse bodyException = GlobalResponse.builder()
                                        .statusCode(HttpStatus.NOT_FOUND.value())
                                        .message(rnfe.getMessage())
                                        .build();
        return new ResponseEntity<>(bodyException,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GlobalResponse> tratamientoRNFE(BadRequestException bre){
        GlobalResponse bodyException = GlobalResponse.builder()
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .message(bre.getMessage())
                                    .build();
        return new ResponseEntity<>(bodyException,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductAlreadyOnRepositoryException.class)
    public ResponseEntity<GlobalResponse> tratamientoRNFE(ProductAlreadyOnRepositoryException paore){
        GlobalResponse bodyException = GlobalResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value()).message(paore.getMessage()).build();
        return new ResponseEntity<>(bodyException,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        GlobalResponse bodyException = GlobalResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Parametros no validos").response(errors).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bodyException);
    }

    @ExceptionHandler(NotValidCategory.class)
    public ResponseEntity<GlobalResponse> tratamientoRNFE(NotValidCategory bre){
        GlobalResponse bodyException = GlobalResponse.builder().statusCode(HttpStatus.CONFLICT.value())
                .message(bre.getMessage()).build();
        return new ResponseEntity<>(bodyException,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DeleteOperationException.class)
    public ResponseEntity<GlobalResponse> tratamientoRNFE(DeleteOperationException bre){
        GlobalResponse bodyException = GlobalResponse.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(bre.getMessage()).build();
        return new ResponseEntity<>(bodyException,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyOnRepositoryException.class)
    public ResponseEntity<GlobalResponse> tratamientoRNFE(UserAlreadyOnRepositoryException uaor){
        GlobalResponse bodyException = GlobalResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value()).message(uaor.getMessage()).build();
        return new ResponseEntity<>(bodyException,HttpStatus.CONFLICT);
    }
}
