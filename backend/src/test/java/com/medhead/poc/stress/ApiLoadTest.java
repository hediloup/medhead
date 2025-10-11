package com.medhead.poc.stress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.model.AllocationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "LOAD_TESTS_ENABLED", matches = "true")
class ApiLoadTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "http://localhost";

    @Test
    void testApiLoad_ConcurrentUsers() throws InterruptedException {
        // Given
        int numberOfUsers = 100;
        int requestsPerUser = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        
        // When
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfUsers; i++) {
            final int userIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerUser; j++) {
                        long requestStart = System.currentTimeMillis();
                        
                        AllocationRequest request = new AllocationRequest();
                        request.setSpecialty("Cardiology");
                        request.setLatitude(51.5074 + (Math.random() - 0.5) * 0.1);
                        request.setLongitude(-0.1278 + (Math.random() - 0.5) * 0.1);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                baseUrl + ":" + port + "/api/allocate",
                                HttpMethod.POST,
                                entity,
                                String.class
                        );

                        long requestDuration = System.currentTimeMillis() - requestStart;
                        totalResponseTime.addAndGet(requestDuration);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                        
                        // Small delay between requests
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all users to complete
        boolean completed = latch.await(300, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;
        
        executor.shutdown();

        // Then
        assertThat(completed).isTrue();
        
        int totalRequests = numberOfUsers * requestsPerUser;
        double successRate = (double) successCount.get() / totalRequests;
        double averageResponseTime = (double) totalResponseTime.get() / totalRequests;
        double throughput = (double) totalRequests / (totalTime / 1000.0);

        // Assertions for load testing
        assertThat(successRate).isGreaterThan(0.95); // 95% success rate
        assertThat(averageResponseTime).isLessThan(1000); // Average response time under 1 second
        assertThat(throughput).isGreaterThan(10); // At least 10 requests per second

        System.out.println("=== API Load Test Results ===");
        System.out.println("Total Users: " + numberOfUsers);
        System.out.println("Requests per User: " + requestsPerUser);
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + errorCount.get());
        System.out.println("Success Rate: " + String.format("%.2f%%", successRate * 100));
        System.out.println("Average Response Time: " + String.format("%.2f ms", averageResponseTime));
        System.out.println("Total Test Duration: " + totalTime + " ms");
        System.out.println("Throughput: " + String.format("%.2f requests/second", throughput));
    }

    @Test
    void testApiLoad_SustainedLoad() throws InterruptedException {
        // Given
        int durationMinutes = 5;
        int requestsPerSecond = 20;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        
        // When
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (durationMinutes * 60 * 1000);
        
        while (System.currentTimeMillis() < endTime) {
            for (int i = 0; i < requestsPerSecond; i++) {
                executor.submit(() -> {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        AllocationRequest request = new AllocationRequest();
                        request.setSpecialty("Cardiology");
                        request.setLatitude(51.5074 + (Math.random() - 0.5) * 0.1);
                        request.setLongitude(-0.1278 + (Math.random() - 0.5) * 0.1);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                baseUrl + ":" + port + "/api/allocate",
                                HttpMethod.POST,
                                entity,
                                String.class
                        );

                        long requestDuration = System.currentTimeMillis() - requestStart;
                        totalResponseTime.addAndGet(requestDuration);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    }
                });
            }
            
            // Wait for 1 second before next batch
            Thread.sleep(1000);
        }
        
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Then
        int totalRequests = successCount.get() + errorCount.get();
        double successRate = (double) successCount.get() / totalRequests;
        double averageResponseTime = (double) totalResponseTime.get() / totalRequests;
        double actualThroughput = (double) totalRequests / (totalTime / 1000.0);

        // Assertions for sustained load
        assertThat(successRate).isGreaterThan(0.98); // 98% success rate for sustained load
        assertThat(averageResponseTime).isLessThan(500); // Average response time under 500ms
        assertThat(actualThroughput).isGreaterThan(15); // At least 15 requests per second sustained

        System.out.println("=== Sustained Load Test Results ===");
        System.out.println("Test Duration: " + durationMinutes + " minutes");
        System.out.println("Target Load: " + requestsPerSecond + " requests/second");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + errorCount.get());
        System.out.println("Success Rate: " + String.format("%.2f%%", successRate * 100));
        System.out.println("Average Response Time: " + String.format("%.2f ms", averageResponseTime));
        System.out.println("Actual Throughput: " + String.format("%.2f requests/second", actualThroughput));
    }

    @Test
    void testApiLoad_SpikeTest() throws InterruptedException {
        // Given
        int normalLoad = 10; // requests per second
        int spikeLoad = 100; // requests per second during spike
        int spikeDurationSeconds = 30;
        
        ExecutorService executor = Executors.newFixedThreadPool(200);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        
        // When - Normal load phase
        System.out.println("Starting normal load phase...");
        long normalStart = System.currentTimeMillis();
        long normalEnd = normalStart + (60 * 1000); // 1 minute normal load
        
        while (System.currentTimeMillis() < normalEnd) {
            for (int i = 0; i < normalLoad; i++) {
                submitRequest(executor, successCount, errorCount, totalResponseTime);
            }
            Thread.sleep(1000);
        }
        
        // Spike load phase
        System.out.println("Starting spike load phase...");
        long spikeStart = System.currentTimeMillis();
        long spikeEnd = spikeStart + (spikeDurationSeconds * 1000);
        
        while (System.currentTimeMillis() < spikeEnd) {
            for (int i = 0; i < spikeLoad; i++) {
                submitRequest(executor, successCount, errorCount, totalResponseTime);
            }
            Thread.sleep(1000);
        }
        
        // Recovery phase
        System.out.println("Starting recovery phase...");
        long recoveryStart = System.currentTimeMillis();
        long recoveryEnd = recoveryStart + (60 * 1000); // 1 minute recovery
        
        while (System.currentTimeMillis() < recoveryEnd) {
            for (int i = 0; i < normalLoad; i++) {
                submitRequest(executor, successCount, errorCount, totalResponseTime);
            }
            Thread.sleep(1000);
        }
        
        executor.shutdown();
        executor.awaitTermination(120, TimeUnit.SECONDS);
        
        // Then
        int totalRequests = successCount.get() + errorCount.get();
        double successRate = (double) successCount.get() / totalRequests;
        double averageResponseTime = (double) totalResponseTime.get() / totalRequests;

        // Assertions for spike test
        assertThat(successRate).isGreaterThan(0.90); // 90% success rate during spike
        assertThat(averageResponseTime).isLessThan(2000); // Average response time under 2 seconds

        System.out.println("=== Spike Test Results ===");
        System.out.println("Normal Load: " + normalLoad + " requests/second");
        System.out.println("Spike Load: " + spikeLoad + " requests/second");
        System.out.println("Spike Duration: " + spikeDurationSeconds + " seconds");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + errorCount.get());
        System.out.println("Success Rate: " + String.format("%.2f%%", successRate * 100));
        System.out.println("Average Response Time: " + String.format("%.2f ms", averageResponseTime));
    }

    private void submitRequest(ExecutorService executor, AtomicInteger successCount, 
                             AtomicInteger errorCount, AtomicLong totalResponseTime) {
        executor.submit(() -> {
            try {
                long requestStart = System.currentTimeMillis();
                
                AllocationRequest request = new AllocationRequest();
                request.setSpecialty("Cardiology");
                request.setLatitude(51.5074 + (Math.random() - 0.5) * 0.1);
                request.setLongitude(-0.1278 + (Math.random() - 0.5) * 0.1);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        baseUrl + ":" + port + "/api/allocate",
                        HttpMethod.POST,
                        entity,
                        String.class
                );

                long requestDuration = System.currentTimeMillis() - requestStart;
                totalResponseTime.addAndGet(requestDuration);

                if (response.getStatusCode().is2xxSuccessful()) {
                    successCount.incrementAndGet();
                } else {
                    errorCount.incrementAndGet();
                }
            } catch (Exception e) {
                errorCount.incrementAndGet();
            }
        });
    }
}
