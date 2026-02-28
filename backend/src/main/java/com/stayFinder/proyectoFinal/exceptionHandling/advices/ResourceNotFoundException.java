package com.stayFinder.proyectoFinal.exceptionHandling.advices;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
