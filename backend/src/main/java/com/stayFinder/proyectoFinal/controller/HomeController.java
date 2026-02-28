package com.stayFinder.proyectoFinal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("app", "Gestion de reservas Stayfinder 2025");
        response.put("version", "1.0.0");
        response.put("message", " :) Backend corriendo correctamente en localhost:8080");
        return response;
    }
}
