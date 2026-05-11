package com.stayFinder.proyectoFinal.qa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TestRailReporter {

    private final String baseUrl;
    private final String authHeader;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private Long runId;

    public TestRailReporter() {
        // Usa la URL real de tu instancia
        String url = System.getenv("TESTRAIL_URL");
        if (url == null || url.isEmpty()) {
            url = "https://stayfinderpro.testrail.io";
        }
        this.baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        // Usa las llaves que configuró tu compañero
        String user = System.getenv("TESTRAIL_USER");
        String apiKey = System.getenv("TESTRAIL_API_KEY");
        
        String authString = user + ":" + apiKey;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
        
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void createTestRun(Long projectId, String name) {
        try {
            String endpoint = baseUrl + "/index.php?/api/v2/add_run/" + projectId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("include_all", true);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                this.runId = root.get("id").asLong();
                System.out.println("[TestRail] Conectado a Proyecto ID: " + projectId);
                System.out.println("[TestRail] Nuevo Run Creado ID: " + this.runId);
            }
        } catch (Exception e) {
            System.err.println("[TestRail Error] No se pudo conectar al proyecto real: " + e.getMessage());
        }
    }

    public void addResultForCase(Long caseId, boolean passed, String comment) {
        if (this.runId == null) return;
        
        try {
            String endpoint = baseUrl + "/index.php?/api/v2/add_result_for_case/" + this.runId + "/" + caseId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("status_id", passed ? 1 : 5); // 1 = Passed
            body.put("comment", comment);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
            System.out.println("[TestRail] Sincronizado: Caso C" + caseId + " -> " + (passed ? "VERDE ✅" : "ROJO ❌"));
        } catch (Exception e) {
            System.err.println("[TestRail Error] Error al reportar caso C" + caseId);
        }
    }
}
