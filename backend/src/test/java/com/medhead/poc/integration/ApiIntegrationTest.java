package com.medhead.poc.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

/**
 * Test d'intégration pour vérifier que l'API REST fonctionne correctement.
 */
@RunWith(SpringRunner.class)
public class ApiIntegrationTest {

    @Test
    public void testAllocationApi() throws Exception {
        // Arrange
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Act
        String url = "http://localhost:8080/api/allocate?specialty=Cardiologie&latitude=51.5009&longitude=-0.1253";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        
        // Assert
        assertEquals(200, responseEntity.getStatusCode().value());
        
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        String hospitalName = jsonNode.get("hospital_name").asText();
        
        assertTrue("La réponse doit contenir le nom de l'hôpital", 
                  hospitalName.contains("Fred Brooks"));
    }
}
