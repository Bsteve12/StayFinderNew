package com.stayFinder.proyectoFinal.services.pagoService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.PagoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PagoResponseDTO;
import com.stayFinder.proyectoFinal.entity.Pago;
import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.mapper.PagoMapper;
import com.stayFinder.proyectoFinal.repository.PagoRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import com.stayFinder.proyectoFinal.services.pagoService.interfaces.PagoServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoServiceInterface {

    private final PagoRepository pagoRepo;
    private final ReservaRepository reservaRepo;
    private final PagoMapper mapper;

    @Override
    public PagoResponseDTO registrarPago(PagoRequestDTO dto) {
        Reserva reserva = reservaRepo.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validación: monto debe coincidir con el precio total de la reserva
        if (!dto.getMonto().equals(reserva.getPrecioTotal())) {
            throw new RuntimeException("El monto del pago no coincide con el precio de la reserva");
        }

        Pago pago = Pago.builder()
                .reserva(reserva)
                .metodo(dto.getMetodoPago())
                .monto(dto.getMonto())
                .estado("PAGADO") // se asume exitoso, luego puedes manejar pendiente/fallido
                .fecha(LocalDateTime.now())
                .build();

        return mapper.toDto(pagoRepo.save(pago));
    }

    @Override
    public List<PagoResponseDTO> listarPagos() {
        return pagoRepo.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public PagoResponseDTO obtenerPagoPorId(Long id) {
        return pagoRepo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }
}
