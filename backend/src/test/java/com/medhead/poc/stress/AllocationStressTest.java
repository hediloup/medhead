package com.medhead.poc.stress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.poc.model.AllocationRequest;
import com.medhead.poc.model.AllocationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "STRESS_TESTS_ENABLED", matches = "true")
class AllocationStressTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "http://localhost";

    @Test
    void testConcurrentRequests_ShouldHandleMultipleUsers() throws InterruptedException {
        // Given
        int numberOfThreads = 50;
        int requestsPerThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        long startTime = System.currentTimeMillis();
                        
                        AllocationRequest request = new AllocationRequest();
                        request.setSpecialty("Cardiology");
                        request.setLatitude(51.5074 + (Math.random() - 0.5) * 0.1); // Random location around London
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

                        long responseTime = System.currentTimeMillis() - startTime;
                        responseTimes.add(responseTime);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        // Then
        int totalRequests = numberOfThreads * requestsPerThread;
        assertThat(successCount.get()).isGreaterThan((int)(totalRequests * 0.9)); // 90% success rate
        assertThat(errorCount.get()).isLessThan((int)(totalRequests * 0.1)); // Less than 10% errors
        
        // Performance assertions
        double averageResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        assertThat(averageResponseTime).isLessThan(2000.0); // Average response time under 2 seconds
        
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        assertThat(maxResponseTime).isLessThan(5000L); // Max response time under 5 seconds

        System.out.println("Stress Test Results:");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Errors: " + errorCount.get());
        System.out.println("Average Response Time: " + averageResponseTime + "ms");
        System.out.println("Max Response Time: " + maxResponseTime + "ms");
    }

    @Test
    void testHighLoad_ShouldMaintainPerformance() throws InterruptedException {
        // Given
        int numberOfRequests = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        // When
        long testStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfRequests; i++) {
            executor.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    AllocationRequest request = new AllocationRequest();
                    request.setSpecialty("Cardiology");
                    request.setLatitude(51.5074);
                    request.setLongitude(-0.1278);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);

                    ResponseEntity<AllocationResponse> response = restTemplate.exchange(
                            baseUrl + ":" + port + "/api/allocate",
                            HttpMethod.POST,
                            entity,
                            AllocationResponse.class
                    );

                    long responseTime = System.currentTimeMillis() - startTime;
                    responseTimes.add(responseTime);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Log error but continue
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all requests to complete
        latch.await(120, TimeUnit.SECONDS);
        executor.shutdown();

        long testDuration = System.currentTimeMillis() - testStartTime;

        // Then
        double successRate = (double) successCount.get() / numberOfRequests;
        assertThat(successRate).isGreaterThan(0.95); // 95% success rate

        double throughput = (double) numberOfRequests / (testDuration / 1000.0);
        assertThat(throughput).isGreaterThan(50.0); // At least 50 requests per second

        double averageResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        assertThat(averageResponseTime).isLessThan(1000.0); // Average response time under 1 second

        System.out.println("High Load Test Results:");
        System.out.println("Total Requests: " + numberOfRequests);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Success Rate: " + (successRate * 100) + "%");
        System.out.println("Test Duration: " + testDuration + "ms");
        System.out.println("Throughput: " + throughput + " requests/second");
        System.out.println("Average Response Time: " + averageResponseTime + "ms");
    }

    @Test
    void testMemoryUsage_ShouldNotLeak() throws InterruptedException {
        // Given
        int numberOfRequests = 500;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfRequests);

        // Get initial memory usage
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // When
        for (int i = 0; i < numberOfRequests; i++) {
            executor.submit(() -> {
                try {
                    AllocationRequest request = new AllocationRequest();
                    request.setSpecialty("Cardiology");
                    request.setLatitude(51.5074 + (Math.random() - 0.5) * 0.01);
                    request.setLongitude(-0.1278 + (Math.random() - 0.5) * 0.01);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<AllocationRequest> entity = new HttpEntity<>(request, headers);

                    restTemplate.exchange(
                            baseUrl + ":" + port + "/api/allocate",
                            HttpMethod.POST,
                            entity,
                            String.class
                    );
                } catch (Exception e) {
                    // Log error but continue
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for completion
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        // Force garbage collection
        System.gc();
        Thread.sleep(1000);

        // Then
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        // Memory increase should be reasonable (less than 100MB)
        assertThat(memoryIncrease).isLessThan(100L * 1024 * 1024);

        System.out.println("Memory Usage Test Results:");
        System.out.println("Initial Memory: " + (initialMemory / 1024 / 1024) + "MB");
        System.out.println("Final Memory: " + (finalMemory / 1024 / 1024) + "MB");
        System.out.println("Memory Increase: " + (memoryIncrease / 1024 / 1024) + "MB");
    }
}
