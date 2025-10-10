package com.medhead.poc.bdd.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Hooks exécutés avant et après chaque scénario.  
 * Vous pouvez y initialiser des ressources (base de données, serveurs) ou
 * nettoyer l’état après chaque test.
 */
public class Hooks {

    @Before
    public void setUp() {
        // Code d’initialisation global avant chaque scénario
    }

    @After
    public void tearDown() {
        // Code de nettoyage global après chaque scénario
    }
}