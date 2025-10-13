# 📊 Analyse des Performances - MedHead Hospital Allocation

## 🚨 **Résultats du Test de Validation**

### **❌ Objectifs Non Atteints :**
- **P(95)** : 532.52ms (objectif < 200ms) ❌
- **P(99)** : 612.73ms (objectif < 500ms) ❌
- **Taux d'erreur** : 45.49% (objectif < 1%) ❌
- **Temps de réponse moyen** : 175.3ms (objectif < 100ms) ❌
- **Réponses lentes** : 38.55% (objectif < 5%) ❌

### **📈 Métriques Observées :**
- **Débit réel** : 1,851 req/s (objectif 800 req/s) ✅
- **Requêtes totales** : 492,070
- **Échecs** : 223,890 (45.49%)
- **Succès** : 268,180 (54.51%)

## 🔍 **Diagnostic des Problèmes**

### **1. Goulots d'Étranglement Identifiés :**

#### **A. Base de Données :**
- **Requêtes lentes** : Calculs de distance en temps réel
- **Pas d'index** : Recherche par spécialité et localisation
- **Pas de cache** : Requêtes répétitives
- **Pool de connexions** : Possiblement insuffisant

#### **B. Calculs de Distance :**
- **Algorithme** : Formule de Haversine pour chaque hôpital
- **Complexité** : O(n) pour chaque requête
- **Pas de pré-calcul** : Distances calculées à la volée

#### **C. API Externes :**
- **Geocoding** : Appels à des services externes
- **Timeout** : Pas de gestion des timeouts
- **Pas de cache** : Requêtes répétitives

#### **D. Configuration JVM :**
- **Mémoire** : Possiblement insuffisante
- **GC** : Garbage Collection non optimisé
- **Threads** : Pool de threads insuffisant

## 🛠️ **Plan d'Optimisation**

### **Phase 1 : Optimisations Immédiates (1-2 jours)**

#### **1.1 Base de Données :**
```sql
-- Index sur les colonnes critiques
CREATE INDEX idx_hospital_specialty ON hospitals(specialty);
CREATE INDEX idx_hospital_location ON hospitals(latitude, longitude);
CREATE INDEX idx_hospital_beds ON hospitals(available_beds);
CREATE INDEX idx_hospital_specialty_beds ON hospitals(specialty, available_beds);

-- Optimisation des requêtes
EXPLAIN ANALYZE SELECT h.id, h.name, h.latitude, h.longitude, h.available_beds
FROM hospitals h 
WHERE h.specialty = 'Cardiology' 
AND h.available_beds > 0
ORDER BY h.latitude, h.longitude;
```

#### **1.2 Configuration Spring Boot :**
```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
  
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
        batch_versioned_data: true
        connection:
          provider_disables_autocommit: true

server:
  tomcat:
    threads:
      max: 200
      min-spare: 20
    max-connections: 10000
    accept-count: 1000
    connection-timeout: 20000
```

#### **1.3 Configuration JVM :**
```bash
# Variables d'environnement
export JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat"
```

### **Phase 2 : Cache et Optimisations (3-5 jours)**

#### **2.1 Cache Redis :**
```java
// Configuration Redis
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}

// Cache des hôpitaux
@Cacheable(value = "hospitals", key = "#specialty")
public List<Hospital> getHospitalsBySpecialty(String specialty) {
    return hospitalRepository.findBySpecialty(specialty);
}

// Cache des distances
@Cacheable(value = "distances", key = "#lat + '_' + #lng + '_' + #hospitalId")
public double calculateDistance(double lat, double lng, Long hospitalId) {
    // Calcul de distance
}
```

#### **2.2 Pré-calcul des Distances :**
```java
// Service de pré-calcul
@Service
public class DistancePrecalculationService {
    
    @Scheduled(fixedRate = 3600000) // Toutes les heures
    public void precalculateDistances() {
        List<Hospital> hospitals = hospitalRepository.findAll();
        List<Location> commonLocations = getCommonLocations();
        
        for (Location location : commonLocations) {
            for (Hospital hospital : hospitals) {
                double distance = calculateDistance(
                    location.getLatitude(), 
                    location.getLongitude(),
                    hospital.getLatitude(), 
                    hospital.getLongitude()
                );
                
                // Stocker en cache
                cacheService.putDistance(location, hospital, distance);
            }
        }
    }
}
```

### **Phase 3 : Optimisations Avancées (1-2 semaines)**

#### **3.1 Base de Données Avancée :**
```sql
-- Table de pré-calcul des distances
CREATE TABLE hospital_distances (
    id BIGSERIAL PRIMARY KEY,
    hospital_id BIGINT REFERENCES hospitals(id),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    distance_km DECIMAL(8, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hospital_distances_location ON hospital_distances(latitude, longitude);
CREATE INDEX idx_hospital_distances_hospital ON hospital_distances(hospital_id);

-- Vue matérialisée pour les requêtes rapides
CREATE MATERIALIZED VIEW hospital_availability AS
SELECT 
    h.id,
    h.name,
    h.specialty,
    h.available_beds,
    h.latitude,
    h.longitude
FROM hospitals h
WHERE h.available_beds > 0;

CREATE INDEX idx_hospital_availability_specialty ON hospital_availability(specialty);
```

#### **3.2 Optimisation des Algorithmes :**
```java
// Service optimisé
@Service
public class OptimizedAllocationService {
    
    public AllocationResponse findBestHospital(AllocationRequest request) {
        // 1. Cache lookup
        String cacheKey = request.getSpecialty() + "_" + request.getLatitude() + "_" + request.getLongitude();
        AllocationResponse cached = cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 2. Requête optimisée avec index
        List<Hospital> hospitals = hospitalRepository.findAvailableHospitalsBySpecialty(
            request.getSpecialty()
        );
        
        // 3. Calcul de distance optimisé
        Hospital bestHospital = findNearestHospital(hospitals, request);
        
        // 4. Cache du résultat
        AllocationResponse response = buildResponse(bestHospital, request);
        cacheService.put(cacheKey, response, Duration.ofMinutes(5));
        
        return response;
    }
    
    private Hospital findNearestHospital(List<Hospital> hospitals, AllocationRequest request) {
        return hospitals.parallelStream()
            .min(Comparator.comparing(h -> calculateDistance(
                request.getLatitude(), 
                request.getLongitude(),
                h.getLatitude(), 
                h.getLongitude()
            )))
            .orElse(null);
    }
}
```

### **Phase 4 : Monitoring et Tuning (Continue)**

#### **4.1 Métriques Application :**
```java
// Micrometer metrics
@Component
public class AllocationMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Timer allocationTimer;
    private final Counter allocationCounter;
    private final Gauge availableHospitals;
    
    public AllocationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.allocationTimer = Timer.builder("hospital.allocation.duration")
            .description("Time taken to allocate hospital")
            .register(meterRegistry);
        this.allocationCounter = Counter.builder("hospital.allocation.requests")
            .description("Total allocation requests")
            .register(meterRegistry);
    }
    
    public void recordAllocation(Duration duration, boolean success) {
        allocationTimer.record(duration);
        allocationCounter.increment(Tags.of("success", String.valueOf(success)));
    }
}
```

#### **4.2 Configuration de Production :**
```yaml
# application-prod.yml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    com.medhead.poc: INFO
    org.springframework.web: WARN
    org.hibernate.SQL: WARN
    org.hibernate.type.descriptor.sql.BasicBinder: WARN
```

## 📊 **Tests de Validation**

### **Script de Test Optimisé :**
```bash
# Test avec charge progressive
k6 run test-progressive.js

# Test avec charge fixe optimisée
k6 run test-optimized.js

# Test de validation finale
k6 run test-validation.js
```

### **Objectifs de Performance :**
- **P(95)** : < 200ms
- **P(99)** : < 500ms
- **Taux d'erreur** : < 1%
- **Débit** : 800 req/s soutenues
- **Temps de réponse moyen** : < 100ms

## 🎯 **Plan d'Exécution**

### **Semaine 1 :**
- [ ] Optimisation de la base de données (index, requêtes)
- [ ] Configuration Spring Boot et JVM
- [ ] Tests de validation

### **Semaine 2 :**
- [ ] Implémentation du cache Redis
- [ ] Pré-calcul des distances
- [ ] Tests de charge

### **Semaine 3 :**
- [ ] Optimisations avancées
- [ ] Monitoring et métriques
- [ ] Tests de production

### **Semaine 4 :**
- [ ] Tuning final
- [ ] Documentation
- [ ] Déploiement

## 📈 **Métriques de Succès**

### **Avant Optimisation :**
- P(95) : 532.52ms
- Taux d'erreur : 45.49%
- Débit : 1,851 req/s (instable)

### **Après Optimisation (Objectif) :**
- P(95) : < 200ms
- Taux d'erreur : < 1%
- Débit : 800 req/s (stable)

## 🔧 **Outils de Monitoring**

### **Application :**
- **Micrometer** : Métriques JVM et application
- **Prometheus** : Collecte des métriques
- **Grafana** : Visualisation des dashboards

### **Système :**
- **htop** : CPU et mémoire
- **iostat** : I/O disque
- **netstat** : Réseau

### **Base de Données :**
- **pg_stat_activity** : Requêtes actives
- **pg_stat_database** : Statistiques globales
- **EXPLAIN ANALYZE** : Analyse des requêtes

## 📚 **Ressources**

- **Spring Boot Performance** : https://spring.io/guides/gs/spring-boot-performance/
- **PostgreSQL Tuning** : https://wiki.postgresql.org/wiki/Performance_Optimization
- **JVM Tuning** : https://docs.oracle.com/en/java/javase/11/gctuning/
- **Redis Caching** : https://redis.io/docs/manual/patterns/distributed-locks/
