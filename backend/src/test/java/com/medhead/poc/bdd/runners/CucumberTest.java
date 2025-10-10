package com.medhead.poc.bdd.runners;

import io.cucumber.junit.platform.engine.Cucumber;

/**
 * Runner JUnit pour lancer les scénarios Cucumber.  
 *
 * Cette classe reste volontairement vide : l’annotation {@link Cucumber}
 * déclenche la recherche et l’exécution des fichiers `.feature` présents
 * sous `src/test/resources/features`.
 */
@Cucumber
public class CucumberTest {
    // Aucun contenu requis. Cucumber se charge de tout.
}