package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.PagoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PagoResponseDTO;
import com.stayFinder.proyectoFinal.services.pagoService.interfaces.PagoServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoServiceInterface pagoService;

    @GetMapping
    @Operation(summary = "Listar pagos realizados")
    @ApiResponse(responseCode = "200", description = "Listado de pagos")
    public List<PagoResponseDTO> listarPagos() {
        return pagoService.listarPagos();
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo pago")
    @ApiResponse(responseCode = "201", description = "Pago registrado")
    public ResponseEntity<PagoResponseDTO> crearPago(@RequestBody PagoRequestDTO dto) {
        return ResponseEntity.ok(pagoService.registrarPago(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago por ID")
    @ApiResponse(responseCode = "200", description = "Pago encontrado")
    public ResponseEntity<PagoResponseDTO> obtenerPago(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorId(id));
    }
}
