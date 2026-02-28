package com.stayFinder.proyectoFinal.exceptionHandling.advices;

import java.time.LocalDateTime;

public record ErrorResponseMessage(
        boolean success,
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
