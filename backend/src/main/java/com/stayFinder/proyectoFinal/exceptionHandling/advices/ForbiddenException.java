package com.stayFinder.proyectoFinal.exceptionHandling.advices;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
