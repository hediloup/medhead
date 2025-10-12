# 🏗️ Architecture des Pipelines CI/CD MedHead

## 📊 Diagramme d'Architecture

```mermaid
graph TB
    subgraph "🔄 CI - Intégration Continue"
        A[Push/PR] --> B[Tests Backend]
        A --> C[Tests Frontend]
        A --> D[Tests BDD]
        A --> E[Tests E2E]
        A --> F[Analyse Qualité]
        A --> G[Build Docker]
        
        B --> H[Rapports CI]
        C --> H
        D --> H
        E --> H
        F --> H
        G --> H
    end
    
    subgraph "🚀 CD - Livraison Continue"
        I[Push main] --> J[Tests Performance]
        J --> K[Déploiement Staging]
        K --> L[Tests Régression]
        L --> M[Déploiement Production]
        M --> N[Monitoring Post-Deploy]
    end
    
    subgraph "🔒 Security - Sécurité"
        O[Push/PR/Schedule] --> P[Audit Dépendances]
        O --> Q[Analyse SAST]
        O --> R[Scan Vulnérabilités]
        O --> S[Scan Secrets]
        O --> T[Tests DAST]
        
        P --> U[Rapports Sécurité]
        Q --> U
        R --> U
        S --> U
        T --> U
    end
    
    subgraph "🏷️ Release - Release Automatique"
        V[Tag v*.*.*] --> W[Validation Version]
        W --> X[Tests Pré-Release]
        X --> Y[Build Images Release]
        Y --> Z[Création Release GitHub]
        Z --> AA[Déploiement Production]
    end
    
    subgraph "🌐 Environnements"
        BB[Staging Environment]
        CC[Production Environment]
        DD[Monitoring & Alertes]
    end
    
    H --> BB
    K --> BB
    M --> CC
    AA --> CC
    N --> DD
    
    style A fill:#e1f5fe
    style I fill:#e8f5e8
    style O fill:#fff3e0
    style V fill:#f3e5f5
    style CC fill:#ffebee
```

## 🔄 Flux de Données Détaillé

```mermaid
sequenceDiagram
    participant Dev as 👨‍💻 Développeur
    participant GH as 📚 GitHub
    participant CI as 🔄 CI Pipeline
    participant CD as 🚀 CD Pipeline
    participant Staging as 🚀 Staging
    participant Prod as 🌟 Production
    participant Monitor as 📊 Monitoring
    
    Dev->>GH: Push sur develop
    GH->>CI: Déclenche CI
    CI->>CI: Tests Backend
    CI->>CI: Tests Frontend
    CI->>CI: Tests BDD/E2E
    CI->>CI: Analyse Qualité
    CI->>GH: Build Images Docker
    
    Dev->>GH: Push sur main
    GH->>CD: Déclenche CD
    CD->>CD: Tests Performance
    CD->>Staging: Déploiement Staging
    CD->>Staging: Tests Régression
    CD->>Prod: Déploiement Production
    CD->>Monitor: Monitoring Post-Deploy
    
    Dev->>GH: Tag v1.2.3
    GH->>CD: Déclenche Release
    CD->>CD: Validation Version
    CD->>CD: Tests Pré-Release
    CD->>GH: Création Release
    CD->>Prod: Déploiement Production
```

## 🏗️ Architecture Technique

### Stack Technologique

```mermaid
graph LR
    subgraph "🛠️ Outils CI/CD"
        A[GitHub Actions]
        B[Docker]
        C[Docker Compose]
    end
    
    subgraph "🧪 Tests"
        D[JUnit + Mockito]
        E[Jasmine + Karma]
        F[Cucumber + Gherkin]
        G[Cypress]
    end
    
    subgraph "🔍 Qualité & Sécurité"
        H[SonarCloud]
        I[Snyk]
        J[CodeQL]
        K[OWASP ZAP]
    end
    
    subgraph "📊 Monitoring"
        L[Prometheus]
        M[Grafana]
        N[AlertManager]
    end
    
    subgraph "🌐 Infrastructure"
        O[Nginx]
        P[PostgreSQL]
        Q[Redis]
        R[GitHub Container Registry]
    end
    
    A --> D
    A --> E
    A --> F
    A --> G
    A --> H
    A --> I
    A --> J
    A --> K
    
    B --> R
    C --> O
    C --> P
    C --> Q
    
    L --> M
    M --> N
```

## 📋 Matrice des Déclencheurs

| Workflow | Push develop | Push main | PR | Tag | Schedule | Manual |
|----------|--------------|-----------|----|----|-----------|---------|
| **CI** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| **CD** | ❌ | ✅ | ❌ | ✅ | ❌ | ✅ |
| **Security** | ✅ | ✅ | ✅ | ❌ | ✅ | ✅ |
| **Release** | ❌ | ❌ | ❌ | ✅ | ❌ | ✅ |

## 🎯 Stratégie de Déploiement

### Blue-Green Deployment

```mermaid
graph TB
    subgraph "🌊 Déploiement Blue-Green"
        A[Version Actuelle - Blue] --> B[Déploiement Green]
        B --> C[Tests Validation]
        C --> D{Validation OK?}
        D -->|Oui| E[Basculement Trafic]
        D -->|Non| F[Rollback]
        E --> G[Version Nouvelle - Green]
        F --> A
    end
    
    style A fill:#e3f2fd
    style G fill:#e8f5e8
    style F fill:#ffebee
```

## 📊 Métriques et KPIs

### Métriques CI/CD

| Métrique | Objectif | Mesure |
|----------|----------|---------|
| **Durée CI** | < 15 min | Temps d'exécution |
| **Durée CD** | < 30 min | Temps de déploiement |
| **Taux Succès** | > 95% | Déploiements réussis |
| **MTTR** | < 10 min | Temps de récupération |
| **Couverture Tests** | > 90% | Backend / > 85% Frontend |

### Métriques Qualité

| Métrique | Objectif | Outil |
|----------|----------|-------|
| **Code Quality** | A | SonarCloud |
| **Vulnérabilités** | 0 Critique | Snyk |
| **Code Smells** | < 10 | SonarCloud |
| **Duplication** | < 3% | SonarCloud |

## 🔐 Sécurité et Conformité

### Pipeline de Sécurité

```mermaid
graph LR
    A[Code] --> B[Scan SAST]
    A --> C[Audit Dépendances]
    A --> D[Scan Secrets]
    B --> E[Rapport Sécurité]
    C --> E
    D --> E
    E --> F{Approuvé?}
    F -->|Oui| G[Déploiement]
    F -->|Non| H[Blocage]
```

### Standards de Sécurité

- **OWASP Top 10** : Protection contre les vulnérabilités courantes
- **CWE** : Common Weakness Enumeration
- **NIST** : Guidelines de sécurité
- **GDPR** : Conformité protection des données

## 🚀 Évolutions Futures

### Roadmap

1. **Phase 1** : Implémentation actuelle ✅
2. **Phase 2** : Intégration Kubernetes
3. **Phase 3** : Multi-environnements
4. **Phase 4** : IA/ML pour optimisation

### Améliorations Prévues

- **Canary Deployments** : Déploiement progressif
- **Feature Flags** : Activation fonctionnalités
- **Auto-scaling** : Ajustement automatique des ressources
- **Chaos Engineering** : Tests de résilience

---

## 📚 Documentation Associée

- **[CI_CD_GUIDE.md](CI_CD_GUIDE.md)** - Guide complet des pipelines
- **[SECRETS_SETUP.md](.github/SECRETS_SETUP.md)** - Configuration des secrets
- **[README_CI_CD.md](README_CI_CD.md)** - Documentation principale
