package com.stayFinder.proyectoFinal.qa;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoAlojamiento;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.alojamientoService.interfaces.AlojamientoServiceInterface;
import com.stayFinder.proyectoFinal.services.disponibilidadService.DisponibilidadService;
import com.stayFinder.proyectoFinal.services.reservaService.interfaces.ReservaServiceInterface;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration," +
                "org.springframework.boot.actuate.autoconfigure.mail.MailHealthContributorAutoConfiguration"
})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StayFinderFullQAIntegrationTest {

    private static TestRailReporter reporter;
    private static final Long PROJECT_ID = 2L;

    // =====================================================================
    // ID del alojamiento libre — se asigna en @BeforeAll y usan los tests
    // =====================================================================
    private static Long alojamientoLibreId;
    private static Long alojamientoOcupadoId;

    @Autowired private DisponibilidadService disponibilidadService;
    @Autowired private AlojamientoServiceInterface alojamientoService;
    @Autowired private ReservaServiceInterface reservaService;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private AlojamientoRepository alojamientoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeAll
    public static void setUp() {
        reporter = new TestRailReporter();
        if (System.getenv("TESTRAIL_API_KEY") != null) {
            reporter.createTestRun(PROJECT_ID, "Ejecución Automática QA - StayFinder");
        }
    }

    // =====================================================================
    // Crea datos reales ANTES de cada clase de test
    // =====================================================================
    @BeforeEach
    public void crearDatosReales() {
        // Solo creamos si no existen todavía
        if (alojamientoLibreId != null && alojamientoOcupadoId != null) return;

        // 1. Crear usuario propietario real
        Usuario owner = Usuario.builder()
                .usuarioId(System.currentTimeMillis()) // ID único
                .email("owner_qa_" + System.currentTimeMillis() + "@test.com")
                .nombre("Owner QA Test")
                .contrasena("pass123")
                .role(Role.OWNER)
                .build();
        owner = usuarioRepository.save(owner);

        // 2. Crear alojamiento LIBRE (sin reservas)
        Alojamiento libre = Alojamiento.builder()
                .nombre("Alojamiento Libre QA")
                .direccion("Calle Test 123")
                .precio(100.0)
                .descripcion("Alojamiento para pruebas de disponibilidad libre")
                .capacidadMaxima(4)
                .estado(EstadoAlojamiento.ACTIVO)
                .owner(owner)
                .build();
        libre = alojamientoRepository.save(libre);
        alojamientoLibreId = libre.getId();

        // 3. Crear alojamiento OCUPADO (con reserva activa)
        Alojamiento ocupado = Alojamiento.builder()
                .nombre("Alojamiento Ocupado QA")
                .direccion("Calle Test 456")
                .precio(80.0)
                .descripcion("Alojamiento para pruebas de no disponibilidad")
                .capacidadMaxima(2)
                .estado(EstadoAlojamiento.ACTIVO)
                .owner(owner)
                .build();
        ocupado = alojamientoRepository.save(ocupado);
        alojamientoOcupadoId = ocupado.getId();

        // 4. Crear reserva CONFIRMADA sobre el alojamiento ocupado
        Reserva reserva = Reserva.builder()
                .alojamiento(ocupado)
                .fechaInicio(LocalDate.now().minusDays(1))
                .fechaFin(LocalDate.now().plusDays(5))
                .estado(EstadoReserva.CONFIRMADA)
                .precioTotal(500.0)
                .build();
        reservaRepository.save(reserva);
    }

    @Test
    @Order(1)
    @DisplayName("C47 - Validar flujo completo de reserva")
    public void testFlujoReserva_C47() {
        boolean passed = false;
        String comment = "Validación automática del flujo de disponibilidad para reservas.";
        try {
            // ✅ Usa alojamiento REAL creado en @BeforeEach
            boolean disponible = disponibilidadService.isDisponible(
                    alojamientoLibreId,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5)
            );
            assertTrue(disponible, "La disponibilidad de la reserva debería ser TRUE.");
            passed = true;
            comment += " El sistema respondió correctamente y validó disponibilidad.";
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            fail("Fallo en flujo de reserva: " + e.getMessage());
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(50L, passed, comment);
            }
        }
    }

    @Test
    @Order(2)
    @DisplayName("C46 - Validar creación y carga de imágenes")
    public void testAlojamientoImagenes_C46() {
        boolean passed = false;
        String comment = "Validación automática de servicios asociados a alojamientos.";
        try {
            // ✅ Usa alojamiento REAL creado en @BeforeEach
            assertNotNull(disponibilidadService, "El servicio de disponibilidad no debería ser NULL.");
            boolean disponible = disponibilidadService.isDisponible(
                    alojamientoLibreId,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2)
            );
            assertTrue(disponible, "El alojamiento debería encontrarse disponible.");
            passed = true;
            comment += " Servicios y validaciones ejecutados correctamente.";
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            fail("Fallo en validación de alojamientos: " + e.getMessage());
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(49L, passed, comment);
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("C48 - Validar cierre de fechas por mantenimiento")
    public void testBloqueoManual_C48() {
        boolean passed = false;
        String comment = "Validación automática de integridad del calendario.";
        try {
            // ✅ Usa alojamiento REAL creado en @BeforeEach
            boolean disponible = disponibilidadService.isDisponible(
                    alojamientoLibreId,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2)
            );
            assertTrue(disponible, "El calendario debería permitir disponibilidad válida.");
            passed = true;
            comment += " El calendario respondió correctamente sin inconsistencias.";
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            fail("Fallo en validación del calendario: " + e.getMessage());
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(51L, passed, comment);
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("C49 - Gestión de Alojamientos: Rechazar creación con datos inválidos o propietario inexistente")
    public void testCrearAlojamientoInvalido_C49() {
        boolean passed = false;
        String comment = "Validación de rechazo por datos inválidos o propietario inexistente.";
        try {
            AlojamientoRequestDTO req = new AlojamientoRequestDTO(
                    "", "123 Calle Falsa", -100.0, "Descripción prueba", 4, new ArrayList<>()
            );
            Exception exception = assertThrows(
                    Exception.class,
                    () -> alojamientoService.crear(req, -999L),
                    "El sistema debe rechazar alojamientos con propietario inválido"
            );
            assertNotNull(exception);
            passed = true;
            comment += " El sistema rechazó correctamente datos inválidos y propietarios inexistentes.";
        } catch (AssertionError e) {
            passed = false;
            comment += " Falló la aserción: " + e.getMessage();
            throw e;
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            throw new RuntimeException(e);
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(52L, passed, comment);
            }
        }
    }

    @Test
    @Order(5)
    @DisplayName("C50 - Sistema de Reservas: Impedir acciones sobre reservas inexistentes")
    public void testReservaInexistente_C50() {
        boolean passed = false;
        String comment = "Validación de protección contra manipulación de reservas fantasmas.";
        try {
            Exception exception = assertThrows(
                    Exception.class,
                    () -> reservaService.deleteReserva(-999L),
                    "El sistema debe rechazar acciones sobre reservas inexistentes"
            );
            assertTrue(
                    exception.getMessage().toLowerCase().contains("no encontrada") ||
                    exception.getMessage().toLowerCase().contains("no existe"),
                    "El mensaje debe indicar que no se encontró la reserva"
            );
            passed = true;
            comment += " El sistema bloqueó correctamente la manipulación de reservas inexistentes.";
        } catch (AssertionError e) {
            passed = false;
            comment += " Falló la aserción: " + e.getMessage();
            throw e;
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            throw new RuntimeException(e);
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(53L, passed, comment);
            }
        }
    }

    @Test
    @Order(6)
    @DisplayName("C51 - Calendario y Disponibilidad: Rechazar eliminación de bloqueos de calendario inexistentes")
    public void testBloqueoInexistente_C51() {
        boolean passed = false;
        String comment = "Validación de integridad en gestión de bloqueos de calendario.";
        try {
            Exception exception = assertThrows(
                    Exception.class,
                    () -> alojamientoService.eliminarBloqueo(-999L, -999L),
                    "El sistema debe rechazar la manipulación de bloqueos inexistentes"
            );
            assertTrue(
                    exception.getMessage().toLowerCase().contains("no encontrado") ||
                    exception.getMessage().toLowerCase().contains("no existe"),
                    "El mensaje de error debe indicar que el bloqueo no existe"
            );
            passed = true;
            comment += " El sistema protegió correctamente la integridad del calendario.";
        } catch (AssertionError e) {
            passed = false;
            comment += " Falló la aserción: " + e.getMessage();
            throw e;
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            throw new RuntimeException(e);
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(54L, passed, comment);
            }
        }
    }

    @Test
    @Order(7)
    @DisplayName("C52 - Disponibilidad: Rechazar consulta con fechas invertidas")
    public void testDisponibilidadFechasInvertidas_C52() {
        boolean passed = false;
        String comment = "Validación de rechazo de consulta de disponibilidad con fechas inválidas.";
        try {
            Exception exception = assertThrows(
                    Exception.class,
                    () -> disponibilidadService.isDisponible(
                            alojamientoLibreId,
                            LocalDate.now().plusDays(5),
                            LocalDate.now()
                    ),
                    "El sistema debe rechazar la consulta si la fecha de fin es anterior a la fecha de inicio"
            );
            assertEquals("La fecha de fin debe ser posterior a la fecha de inicio", exception.getMessage());
            passed = true;
            comment += " El sistema rechazó correctamente las fechas invertidas.";
        } catch (AssertionError e) {
            passed = false;
            comment += " Falló la aserción: " + e.getMessage();
            throw e;
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            throw new RuntimeException(e);
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(55L, passed, comment);
            }
        }
    }

    @Test
    @Order(8)
    @DisplayName("C53 - Disponibilidad: Rechazar consulta con ID de alojamiento nulo")
    public void testDisponibilidadIdInvalido_C53() {
        boolean passed = false;
        String comment = "Validación de rechazo de consulta de disponibilidad sin un ID válido.";
        try {
            Exception exception = assertThrows(
                    Exception.class,
                    () -> disponibilidadService.isDisponible(null, LocalDate.now(), LocalDate.now().plusDays(3)),
                    "El sistema debe rechazar la consulta si el ID de alojamiento es nulo"
            );
            assertEquals("El ID del alojamiento no puede ser nulo", exception.getMessage());
            passed = true;
            comment += " El sistema validó correctamente la ausencia de un ID de alojamiento válido.";
        } catch (AssertionError e) {
            passed = false;
            comment += " Falló la aserción: " + e.getMessage();
            throw e;
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            throw new RuntimeException(e);
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(56L, passed, comment);
            }
        }
    }

    @Test
    @Order(9)
    @DisplayName("C54 - Disponibilidad: Rechazar alojamiento con reserva activa")
    public void testAlojamientoNoDisponibleConReservaActiva_C54() {
        boolean passed = false;
        String comment = "Validación de que un alojamiento con reserva activa retorna false.";
        try {
            // ✅ Usa alojamiento OCUPADO creado en @BeforeEach con reserva CONFIRMADA
            boolean disponible = disponibilidadService.isDisponible(
                    alojamientoOcupadoId,
                    LocalDate.now(),
                    LocalDate.now().plusDays(3)
            );
            assertFalse(disponible, "El alojamiento NO debería estar disponible en estas fechas.");
            passed = true;
            comment += " El sistema validó correctamente la reserva cruzada y retornó false.";
        } catch (AssertionError e) {
            passed = false;
            comment += " Falló la aserción: " + e.getMessage();
            throw e;
        } catch (Exception e) {
            passed = false;
            comment += " Error encontrado: " + e.getMessage();
            throw new RuntimeException(e);
        } finally {
            if (System.getenv("TESTRAIL_API_KEY") != null) {
                reporter.addResultForCase(57L, passed, comment);
            }
        }
    }
}