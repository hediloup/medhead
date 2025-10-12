package com.medhead.poc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test de base pour vérifier que l'application Spring Boot se charge correctement
 * Approche TDD : Test-Driven Development
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class PocApplicationTests {

    @Test
    public void contextLoads() {
        // Ce test vérifie que le contexte Spring Boot se charge sans erreur
        // Si ce test passe, cela signifie que toutes les configurations sont correctes
    }
}
