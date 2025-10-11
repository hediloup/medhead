package com.medhead.poc.bdd.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Hooks executed before and after each scenario.
 * You can initialize resources (database, servers) or
 * clean up state after each test.
 */
public class Hooks {

    @Before
    public void setUp() {
        // Global initialization code before each scenario
    }

    @After
    public void tearDown() {
        // Global cleanup code after each scenario
    }
}