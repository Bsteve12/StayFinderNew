package com.stayFinder.proyectoFinal.exceptionHandling.advices;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionAdvice {

    // Manejo genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseMessage> handleGeneralException(Exception ex, HttpServletRequest request) {
        ErrorResponseMessage response = new ErrorResponseMessage(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Manejo específico: recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseMessage> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponseMessage response = new ErrorResponseMessage(
                false,
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Manejo específico: acceso prohibido
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseMessage> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        ErrorResponseMessage response = new ErrorResponseMessage(
                false,
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
