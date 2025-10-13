package com.medhead.poc.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application metrics using Micrometer
 */
@Configuration
public class MetricsConfig {

    @Bean
    public Counter allocationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("hospital.allocation.total")
                .description("Total number of hospital allocations")
                .register(meterRegistry);
    }

    @Bean
    public Counter allocationErrorCounter(MeterRegistry meterRegistry) {
        return Counter.builder("hospital.allocation.errors")
                .description("Total number of allocation errors")
                .register(meterRegistry);
    }

    @Bean
    public Timer allocationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("hospital.allocation.duration")
                .description("Time taken for hospital allocation")
                .register(meterRegistry);
    }

    @Bean
    public Counter distanceCalculationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("hospital.distance.calculations")
                .description("Total number of distance calculations")
                .register(meterRegistry);
    }

    @Bean
    public Timer distanceCalculationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("hospital.distance.calculation.duration")
                .description("Time taken for distance calculations")
                .register(meterRegistry);
    }
}
