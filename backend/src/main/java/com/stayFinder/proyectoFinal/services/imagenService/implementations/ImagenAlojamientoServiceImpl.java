package com.stayFinder.proyectoFinal.services.imagenService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.ImagenAlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.ImagenAlojamiento;
import com.stayFinder.proyectoFinal.mapper.ImagenAlojamientoMapper;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.ImagenAlojamientoRepository;
import com.stayFinder.proyectoFinal.services.imagenService.interfaces.ImagenAlojamientoServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImagenAlojamientoServiceImpl implements ImagenAlojamientoServiceInterface {

    private final ImagenAlojamientoRepository imagenRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ImagenAlojamientoMapper mapper;

    // Ruta relativa para guardar imágenes
    private static final String UPLOAD_DIR = "uploads/alojamientos/";

    @Override
    public ImagenAlojamientoResponseDTO subirImagen(ImagenAlojamientoRequestDTO dto) throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(dto.getAlojamientoId())
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        ImagenAlojamiento imagen = mapper.toEntity(dto);
        imagen.setAlojamiento(alojamiento);

        ImagenAlojamiento saved = imagenRepository.save(imagen);
        return mapper.toDto(saved);
    }

    @Override
    public List<ImagenAlojamientoResponseDTO> subirImagenes(Long alojamientoId, List<MultipartFile> archivos,
            List<String> nuevasUrls)
            throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        if ((archivos == null || archivos.isEmpty()) && (nuevasUrls == null || nuevasUrls.isEmpty())) {
            throw new Exception("No se adjuntaron imágenes físicas ni URLs");
        }

        List<ImagenAlojamiento> imagenesGuardadas = new ArrayList<>();

        if (archivos != null && !archivos.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile archivo : archivos) {
                    if (!archivo.isEmpty()) {
                        String fileName = "alojamiento_" + alojamientoId + "_" + System.currentTimeMillis() + "_"
                                + archivo.getOriginalFilename();
                        Path destino = uploadPath.resolve(fileName);

                        Files.write(destino, archivo.getBytes());

                        // Guardar ruta relativa como URL para que se pueda servir estáticamente
                        String url = "/api/imagenes-alojamiento/file/" + fileName;

                        ImagenAlojamiento req = new ImagenAlojamiento();
                        req.setAlojamiento(alojamiento);
                        req.setUrl(url);

                        imagenesGuardadas.add(imagenRepository.save(req));
                    }
                }
            } catch (IOException e) {
                throw new Exception("Error al guardar las imágenes: " + e.getMessage(), e);
            }
        }

        if (nuevasUrls != null && !nuevasUrls.isEmpty()) {
            for (String url : nuevasUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    ImagenAlojamiento req = new ImagenAlojamiento();
                    req.setAlojamiento(alojamiento);
                    req.setUrl(url.trim());
                    imagenesGuardadas.add(imagenRepository.save(req));
                }
            }
        }

        return imagenesGuardadas.stream().map(mapper::toDto).toList();
    }

    @Override
    public ImagenAlojamientoResponseDTO obtenerImagen(Long id) throws Exception {
        ImagenAlojamiento imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new Exception("Imagen no encontrada"));
        return mapper.toDto(imagen);
    }

    @Override
    public List<ImagenAlojamientoResponseDTO> listarPorAlojamiento(Long alojamientoId) throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        return alojamiento.getImagenes().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void eliminarImagen(Long id) throws Exception {
        ImagenAlojamiento imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new Exception("Imagen no encontrada"));
        imagenRepository.delete(imagen);
    }
}
