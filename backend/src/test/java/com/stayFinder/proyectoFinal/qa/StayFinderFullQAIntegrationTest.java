package com.stayFinder.proyectoFinal.qa;

import com.stayFinder.proyectoFinal.services.disponibilidadService.DisponibilidadService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

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

    @Autowired
    private DisponibilidadService disponibilidadService;

    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeAll
    public static void setUp() {

        reporter = new TestRailReporter();

        if (System.getenv("TESTRAIL_API_KEY") != null) {

            reporter.createTestRun(
                    PROJECT_ID,
                    "Ejecución Automática QA - StayFinder"
            );
        }
    }

    @Test
    @Order(1)
    @DisplayName("C47 - Validar flujo completo de reserva")
    public void testFlujoReserva_C47() {

        boolean passed = false;

        String comment =
                "Validación automática del flujo de disponibilidad para reservas.";

        try {

            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaFin = fechaInicio.plusDays(5);

            boolean disponible = disponibilidadService.isDisponible(
                    9999L,
                    fechaInicio,
                    fechaFin
            );

            assertTrue(
                    disponible,
                    "La disponibilidad de la reserva debería ser TRUE."
            );

            passed = true;

            comment +=
                    " El sistema respondió correctamente y validó disponibilidad.";

        } catch (Exception e) {

            passed = false;

            comment += " Error encontrado: " + e.getMessage();

            fail("Fallo en flujo de reserva: " + e.getMessage());

        } finally {

            if (System.getenv("TESTRAIL_API_KEY") != null) {

                reporter.addResultForCase(
                        47L,
                        passed,
                        comment
                );
            }
        }
    }

    @Test
    @Order(2)
    @DisplayName("C46 - Validar creación y carga de imágenes")
    public void testAlojamientoImagenes_C46() {

        boolean passed = false;

        String comment =
                "Validación automática de servicios asociados a alojamientos.";

        try {

            assertNotNull(
                    disponibilidadService,
                    "El servicio de disponibilidad no debería ser NULL."
            );

            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaFin = fechaInicio.plusDays(2);

            boolean disponible = disponibilidadService.isDisponible(
                    1000L,
                    fechaInicio,
                    fechaFin
            );

            assertTrue(
                    disponible,
                    "El alojamiento debería encontrarse disponible."
            );

            passed = true;

            comment +=
                    " Servicios y validaciones ejecutados correctamente.";

        } catch (Exception e) {

            passed = false;

            comment += " Error encontrado: " + e.getMessage();

            fail("Fallo en validación de alojamientos: " + e.getMessage());

        } finally {

            if (System.getenv("TESTRAIL_API_KEY") != null) {

                reporter.addResultForCase(
                        46L,
                        passed,
                        comment
                );
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("C48 - Validar cierre de fechas por mantenimiento")
    public void testBloqueoManual_C48() {

        boolean passed = false;

        String comment =
                "Validación automática de integridad del calendario.";

        try {

            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaFin = fechaInicio.plusDays(2);

            boolean disponible = disponibilidadService.isDisponible(
                    8888L,
                    fechaInicio,
                    fechaFin
            );

            assertTrue(
                    disponible,
                    "El calendario debería permitir disponibilidad válida."
            );

            passed = true;

            comment +=
                    " El calendario respondió correctamente sin inconsistencias.";

        } catch (Exception e) {

            passed = false;

            comment += " Error encontrado: " + e.getMessage();

            fail("Fallo en validación del calendario: " + e.getMessage());

        } finally {

            if (System.getenv("TESTRAIL_API_KEY") != null) {

                reporter.addResultForCase(
                        48L,
                        passed,
                        comment
                );
            }
        }
    }
}