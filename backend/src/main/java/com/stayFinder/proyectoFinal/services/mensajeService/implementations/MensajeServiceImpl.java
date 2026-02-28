package com.stayFinder.proyectoFinal.services.mensajeService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.MensajeRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.MensajeResponseDTO;
import com.stayFinder.proyectoFinal.entity.Chat;
import com.stayFinder.proyectoFinal.entity.Mensaje;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.mapper.MensajeMapper;
import com.stayFinder.proyectoFinal.repository.ChatRepository;
import com.stayFinder.proyectoFinal.repository.MensajeRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.mensajeService.interfaces.MensajeServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements MensajeServiceInterface {

    private final MensajeRepository mensajeRepo;
    private final ChatRepository chatRepo;
    private final UsuarioRepository usuarioRepo;
    private final MensajeMapper mensajeMapper;

    @Override
    public MensajeResponseDTO enviarMensaje(MensajeRequestDTO dto) {
        Chat chat = chatRepo.findById(dto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        Usuario remitente = usuarioRepo.findById(dto.getRemitenteId())
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));

        // Validar que el remitente pertenezca al chat
        if (!chat.getUsuario().getId().equals(remitente.getId()) &&
                !chat.getAnfitrion().getId().equals(remitente.getId())) {
            throw new RuntimeException("El remitente no pertenece a este chat");
        }

        Mensaje mensaje = Mensaje.builder()
                .contenido(dto.getContenido())
                .fechaEnvio(LocalDateTime.now())
                .chat(chat)
                .remitente(remitente)
                .build();

        mensajeRepo.save(mensaje);
        return mensajeMapper.toDto(mensaje);
    }

    @Override
    public List<MensajeResponseDTO> listarMensajesPorChat(Long chatId) {
        return mensajeRepo.findAll().stream()
                .filter(m -> m.getChat().getId().equals(chatId))
                .map(mensajeMapper::toDto)
                .toList();
    }
}
