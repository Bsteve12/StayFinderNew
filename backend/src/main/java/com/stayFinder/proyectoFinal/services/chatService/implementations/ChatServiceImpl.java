package com.stayFinder.proyectoFinal.services.chatService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.ChatRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ChatResponseDTO;
import com.stayFinder.proyectoFinal.entity.Chat;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.mapper.ChatMapper;
import com.stayFinder.proyectoFinal.repository.ChatRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.chatService.interfaces.ChatServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatServiceInterface {

    private final ChatRepository chatRepo;
    private final UsuarioRepository usuarioRepo;
    private final ReservaRepository reservaRepo;
    private final ChatMapper chatMapper;

    public ChatServiceImpl(ChatRepository chatRepo,
                           UsuarioRepository usuarioRepo,
                           ReservaRepository reservaRepo,
                           ChatMapper chatMapper) {
        this.chatRepo = chatRepo;
        this.usuarioRepo = usuarioRepo;
        this.reservaRepo = reservaRepo;
        this.chatMapper = chatMapper;
    }

    @Override
    public ChatResponseDTO crearChat(ChatRequestDTO dto) {
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario anfitrion = usuarioRepo.findById(dto.getAnfitrionId())
                .orElseThrow(() -> new RuntimeException("Anfitrión no encontrado"));

        // Validar que exista una reserva CONFIRMADA entre ambos
        boolean tieneReservaConfirmada = reservaRepo.existsByUsuarioAndAlojamientoOwnerAndEstado(
                usuario, anfitrion, EstadoReserva.CONFIRMADA
        );

        if (!tieneReservaConfirmada) {
            throw new RuntimeException("No se puede abrir chat sin reserva confirmada");
        }

        Chat chat = Chat.builder()
                .usuario(usuario)
                .anfitrion(anfitrion)
                .build();

        chatRepo.save(chat);
        return chatMapper.toDto(chat);
    }

    @Override
    public List<ChatResponseDTO> listarChats() {
        return chatRepo.findAll().stream()
                .map(chatMapper::toDto)
                .toList();
    }
}
