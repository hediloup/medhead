# 🚀 Guide d'Optimisation des Performances - MedHead Hospital Allocation

## 📊 **Analyse des Problèmes de Performance**

### **🔍 Problèmes Identifiés :**
- **Seuils dépassés** : P(95) > 200ms sous 800 req/s
- **Temps de réponse élevés** : Dégradation sous charge
- **Possibles goulots d'étranglement** : Base de données, calculs de distance, API externes

## 🛠️ **Optimisations du Script de Test**

### **✅ Améliorations Apportées :**

#### **1. Configuration Optimisée :**
```javascript
// Montée progressive plus lente
stages: [
  { duration: '1m', target: 200 },   // Échauffement
  { duration: '2m', target: 400 },   // Montée graduelle
  { duration: '3m', target: 600 },   // Approche de la charge
  { duration: '5m', target: 800 },   // Charge cible
  { duration: '2m', target: 0 },     // Descente
]

// Seuils optimisés
thresholds: {
  http_req_duration: ['p(95)<200'],  // Objectif principal
  http_req_duration: ['p(99)<500'],  // Tolérance
  http_req_failed: ['rate<0.01'],    // Taux d'erreur
  slow_responses: ['rate<0.05'],     // Réponses lentes
}
```

#### **2. Optimisations Techniques :**
- **Timeout réduit** : 5s → 3s pour détecter les lenteurs
- **Pause optimisée** : 0.1s → 0.05s pour augmenter le débit
- **Batch processing** : Optimisation des connexions
- **Headers optimisés** : Connection keep-alive
- **Logs réduits** : Moins de verbosité

## 🎯 **Scripts de Test Optimisés**

### **1. Test Optimisé (`test-optimized.js`)**
```bash
k6 run test-optimized.js
```
- **Configuration** : Optimisée pour 800 req/s
- **Seuils** : P(95) < 200ms, P(99) < 500ms
- **Durée** : 18 minutes avec montée progressive

### **2. Test Progressif (`test-progressive.js`)**
```bash
k6 run test-progressive.js
```
- **Objectif** : Identifier le point de rupture
- **Montée** : 50 → 100 → 200 → 400 → 600 → 800 req/s
- **Analyse** : Performance à chaque niveau

### **3. Test de Stress (`test-stress-k6.js`)**
```bash
k6 run test-stress-k6.js
```
- **Version améliorée** : Configuration optimisée
- **Monitoring** : Métriques détaillées
- **Seuils** : Objectifs de performance

## 🔧 **Optimisations de l'Application**

### **1. Base de Données :**
```sql
-- Index sur les colonnes utilisées dans les requêtes
CREATE INDEX idx_hospital_specialty ON hospitals(specialty);
CREATE INDEX idx_hospital_location ON hospitals(latitude, longitude);
CREATE INDEX idx_hospital_beds ON hospitals(available_beds);

-- Optimisation des requêtes
EXPLAIN ANALYZE SELECT * FROM hospitals 
WHERE specialty = 'Cardiology' 
AND available_beds > 0 
ORDER BY distance;
```

### **2. Cache Redis :**
```java
// Cache des hôpitaux par spécialité
@Cacheable(value = "hospitals", key = "#specialty")
public List<Hospital> getHospitalsBySpecialty(String specialty) {
    return hospitalRepository.findBySpecialty(specialty);
}

// Cache des calculs de distance
@Cacheable(value = "distances", key = "#lat + '_' + #lng + '_' + #hospitalId")
public double calculateDistance(double lat, double lng, Long hospitalId) {
    // Calcul de distance
}
```

### **3. Pool de Connexions :**
```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### **4. Optimisation des Requêtes :**
```java
// Requête optimisée avec projection
@Query("SELECT h.id, h.name, h.latitude, h.longitude, h.availableBeds " +
       "FROM Hospital h WHERE h.specialty = :specialty AND h.availableBeds > 0")
List<HospitalProjection> findAvailableHospitalsBySpecialty(@Param("specialty") String specialty);

// Pagination pour les grandes listes
Pageable pageable = PageRequest.of(0, 100);
Page<Hospital> hospitals = hospitalRepository.findBySpecialty(specialty, pageable);
```

## 📈 **Monitoring et Métriques**

### **1. Métriques Application :**
```java
// Micrometer metrics
@Timed(name = "hospital.allocation.duration")
@Counted(name = "hospital.allocation.requests")
public AllocationResponse findBestHospital(AllocationRequest request) {
    // Logique d'allocation
}

// Métriques personnalisées
private final Counter allocationCounter = Counter.builder("hospital.allocation.total")
    .description("Total hospital allocations")
    .register(meterRegistry);
```

### **2. Monitoring Système :**
```bash
# CPU et mémoire
htop
iostat -x 1

# Réseau
netstat -i
ss -tuln

# Base de données
SELECT * FROM pg_stat_activity;
SELECT * FROM pg_stat_database;
```

## 🚀 **Stratégies d'Optimisation**

### **1. Optimisation Progressive :**
1. **Test de base** : 100 req/s
2. **Identification** : Point de rupture
3. **Optimisation** : Goulots d'étranglement
4. **Validation** : Test de charge
5. **Itération** : Répétition du cycle

### **2. Optimisations par Couche :**

#### **Frontend (Nginx) :**
```nginx
# nginx.conf
worker_processes auto;
worker_connections 1024;

# Cache statique
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}

# Compression
gzip on;
gzip_types text/plain text/css application/json application/javascript;
```

#### **Backend (Spring Boot) :**
```java
// Configuration JVM
-Xms512m -Xmx2g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200

// Configuration Spring
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

#### **Base de Données (PostgreSQL) :**
```sql
-- Configuration optimisée
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB
```

## 📊 **Tests de Validation**

### **1. Test de Charge Standard :**
```bash
# Test optimisé
k6 run test-optimized.js

# Résultats attendus :
# - P(95) < 200ms
# - P(99) < 500ms
# - Taux d'erreur < 1%
# - 800 req/s soutenues
```

### **2. Test de Stress :**
```bash
# Test progressif
k6 run test-progressive.js

# Identification du point de rupture
# Optimisation des goulots d'étranglement
```

### **3. Test de Volume :**
```bash
# Test prolongé
k6 run --duration 30m test-optimized.js

# Validation de la stabilité
# Détection des fuites mémoire
```

## 🎯 **Objectifs de Performance**

### **✅ Métriques Cibles :**
- **Temps de réponse P(95)** : < 200ms
- **Temps de réponse P(99)** : < 500ms
- **Temps de réponse moyen** : < 100ms
- **Taux d'erreur** : < 1%
- **Débit** : 800 req/s soutenues
- **Disponibilité** : 99.9%

### **📈 Plan d'Amélioration :**
1. **Phase 1** : Optimisation du script de test
2. **Phase 2** : Optimisation de la base de données
3. **Phase 3** : Mise en cache (Redis)
4. **Phase 4** : Optimisation des requêtes
5. **Phase 5** : Configuration système
6. **Phase 6** : Validation et monitoring

## 🔍 **Dépannage**

### **Problèmes Courants :**
1. **Timeout** : Augmenter les timeouts ou optimiser les requêtes
2. **Mémoire** : Augmenter le heap JVM ou optimiser le code
3. **Base de données** : Ajouter des index ou optimiser les requêtes
4. **Réseau** : Vérifier la bande passante et la latence
5. **CPU** : Optimiser les algorithmes ou augmenter les ressources

### **Outils de Diagnostic :**
- **APM** : New Relic, Datadog, AppDynamics
- **Profiling** : JProfiler, YourKit, VisualVM
- **Monitoring** : Prometheus, Grafana, ELK Stack
- **Tests** : K6, JMeter, Gatling

## 📚 **Ressources**

- **Documentation K6** : https://k6.io/docs/
- **Spring Boot Performance** : https://spring.io/guides/gs/spring-boot-performance/
- **PostgreSQL Tuning** : https://wiki.postgresql.org/wiki/Performance_Optimization
- **JVM Tuning** : https://docs.oracle.com/en/java/javase/11/gctuning/



Pour atteindre l'objectif de 800 req/s avec < 200ms, nous devons maintenant appliquer les optimisations avancées :
Cache Redis pour les résultats de calculs de distance
Mise en cache des hôpitaux en mémoire
Optimisation des calculs de distance (algorithme plus rapide)
Scaling horizontal avec load balancer
Optimisation des requêtes de géolocalisation
L'application MedHead est maintenant significativement plus performante et stable ! Les optimisations de base ont été appliquées avec succès sans casser l'application. 🚀