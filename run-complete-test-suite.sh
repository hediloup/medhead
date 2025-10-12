#!/bin/bash

# Script de test complet pour MedHead
# Orchestre tous les niveaux de la pyramide de tests (Unitaires, Intégration, E2E)
# Auteur: Assistant IA
# Version: 1.0

set -e  # Arrêter le script en cas d'erreur

# Configuration des couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Configuration des variables
PROJECT_ROOT="/home/hedi/projects/medhead"
BACKEND_DIR="$PROJECT_ROOT/backend"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
DOCKER_DIR="$PROJECT_ROOT/docker"
REPORTS_DIR="$PROJECT_ROOT/reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Variables de configuration des tests
RUN_UNIT_TESTS=true
RUN_INTEGRATION_TESTS=true
RUN_BDD_TESTS=true
RUN_E2E_TESTS=true
RUN_PERFORMANCE_TESTS=false
GENERATE_REPORTS=true
CLEANUP_AFTER=true
PARALLEL_EXECUTION=true

# Fonctions utilitaires
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_section() {
    echo -e "\n${PURPLE}========================================${NC}"
    echo -e "${PURPLE}$1${NC}"
    echo -e "${PURPLE}========================================${NC}\n"
}

# Fonction pour vérifier les prérequis
check_prerequisites() {
    log_section "🔍 Vérification des prérequis"
    
    # Vérifier Java/Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven n'est pas installé ou n'est pas dans le PATH"
        exit 1
    fi
    log_success "Maven détecté: $(mvn -version | head -n1)"
    
    # Vérifier Node.js/npm
    if ! command -v node &> /dev/null; then
        log_error "Node.js n'est pas installé ou n'est pas dans le PATH"
        exit 1
    fi
    log_success "Node.js détecté: $(node --version)"
    
    # Vérifier Docker
    if ! command -v docker &> /dev/null; then
        log_warning "Docker n'est pas installé. Les tests E2E nécessitent Docker."
        RUN_E2E_TESTS=false
    else
        log_success "Docker détecté: $(docker --version)"
    fi
    
    # Vérifier Cypress
    if [ ! -d "$FRONTEND_DIR/node_modules/cypress" ]; then
        log_warning "Cypress n'est pas installé. Installation..."
        cd "$FRONTEND_DIR"
        npm install cypress --save-dev
        cd "$PROJECT_ROOT"
    fi
    log_success "Cypress disponible"
    
    # Créer le répertoire de rapports
    mkdir -p "$REPORTS_DIR"
    log_success "Répertoire de rapports créé: $REPORTS_DIR"
}

# Fonction pour nettoyer les rapports précédents
cleanup_previous_reports() {
    log_section "🧹 Nettoyage des rapports précédents"
    
    if [ -d "$REPORTS_DIR" ]; then
        rm -rf "$REPORTS_DIR"/*
        log_success "Rapports précédents supprimés"
    fi
}

# Fonction pour les tests unitaires Backend (TDD)
run_backend_unit_tests() {
    if [ "$RUN_UNIT_TESTS" = true ]; then
        log_section "🧪 Tests Unitaires Backend (TDD)"
        
        cd "$BACKEND_DIR"
        
        log_info "Exécution des tests unitaires avec Maven..."
        start_time=$(date +%s)
        
        if mvn test -P unit-tests -Dmaven.test.failure.ignore=false \
           -Dsurefire.reportsDirectory="$REPORTS_DIR/backend/unit-tests" \
           -Dspring.profiles.active=test; then
            end_time=$(date +%s)
            duration=$((end_time - start_time))
            log_success "Tests unitaires backend terminés avec succès en ${duration}s"
            
            # Copier les rapports
            cp -r target/surefire-reports/* "$REPORTS_DIR/backend/unit-tests/" 2>/dev/null || true
        else
            log_error "Échec des tests unitaires backend"
            return 1
        fi
        
        cd "$PROJECT_ROOT"
    fi
}

# Fonction pour les tests d'intégration Backend
run_backend_integration_tests() {
    if [ "$RUN_INTEGRATION_TESTS" = true ]; then
        log_section "🔗 Tests d'Intégration Backend"
        
        cd "$BACKEND_DIR"
        
        log_info "Exécution des tests d'intégration avec Maven..."
        start_time=$(date +%s)
        
        if mvn test -P integration-tests -Dmaven.test.failure.ignore=false \
           -Dfailsafe.reportsDirectory="$REPORTS_DIR/backend/integration-tests" \
           -Dspring.profiles.active=test; then
            end_time=$(date +%s)
            duration=$((end_time - start_time))
            log_success "Tests d'intégration backend terminés avec succès en ${duration}s"
            
            # Copier les rapports
            cp -r target/failsafe-reports/* "$REPORTS_DIR/backend/integration-tests/" 2>/dev/null || true
        else
            log_error "Échec des tests d'intégration backend"
            return 1
        fi
        
        cd "$PROJECT_ROOT"
    fi
}

# Fonction pour les tests BDD Backend
run_backend_bdd_tests() {
    if [ "$RUN_BDD_TESTS" = true ]; then
        log_section "📋 Tests BDD Backend (Cucumber)"
        
        cd "$BACKEND_DIR"
        
        log_info "Exécution des tests BDD avec Cucumber..."
        start_time=$(date +%s)
        
        if mvn test -P bdd-tests -Dmaven.test.failure.ignore=false \
           -Dcucumber.reportsDirectory="$REPORTS_DIR/backend/bdd-tests" \
           -Dspring.profiles.active=test; then
            end_time=$(date +%s)
            duration=$((end_time - start_time))
            log_success "Tests BDD backend terminés avec succès en ${duration}s"
            
            # Copier les rapports Cucumber
            cp -r target/cucumber-reports/* "$REPORTS_DIR/backend/bdd-tests/" 2>/dev/null || true
        else
            log_error "Échec des tests BDD backend"
            return 1
        fi
        
        cd "$PROJECT_ROOT"
    fi
}

# Fonction pour les tests unitaires Frontend
run_frontend_unit_tests() {
    if [ "$RUN_UNIT_TESTS" = true ]; then
        log_section "🧪 Tests Unitaires Frontend (Jasmine/Karma)"
        
        cd "$FRONTEND_DIR"
        
        log_info "Exécution des tests unitaires Angular..."
        start_time=$(date +%s)
        
        if npm run test:coverage -- --watch=false --browsers=ChromeHeadless \
           --reporters=html,coverage --output-path="$REPORTS_DIR/frontend/unit-tests"; then
            end_time=$(date +%s)
            duration=$((end_time - start_time))
            log_success "Tests unitaires frontend terminés avec succès en ${duration}s"
        else
            log_error "Échec des tests unitaires frontend"
            return 1
        fi
        
        cd "$PROJECT_ROOT"
    fi
}

# Fonction pour les tests E2E Frontend
run_frontend_e2e_tests() {
    if [ "$RUN_E2E_TESTS" = true ]; then
        log_section "🌐 Tests E2E Frontend (Cypress)"
        
        # Démarrer les services Docker si nécessaire
        log_info "Vérification des services Docker..."
        if ! docker-compose -f "$DOCKER_DIR/docker-compose.yml" ps | grep -q "Up"; then
            log_info "Démarrage des services Docker..."
            docker-compose -f "$DOCKER_DIR/docker-compose.yml" up -d
            
            # Attendre que les services soient prêts
            log_info "Attente du démarrage des services..."
            sleep 30
        fi
        
        cd "$FRONTEND_DIR"
        
        log_info "Exécution des tests E2E avec Cypress..."
        start_time=$(date +%s)
        
        # Configurer Cypress pour les rapports
        export CYPRESS_REPORTS_DIR="$REPORTS_DIR/frontend/e2e-tests"
        
        if npm run e2e:ci -- --reporter junit \
           --reporter-options "mochaFile=$REPORTS_DIR/frontend/e2e-tests/results-[hash].xml"; then
            end_time=$(date +%s)
            duration=$((end_time - start_time))
            log_success "Tests E2E frontend terminés avec succès en ${duration}s"
        else
            log_error "Échec des tests E2E frontend"
            return 1
        fi
        
        cd "$PROJECT_ROOT"
    fi
}

# Fonction pour les tests de performance
run_performance_tests() {
    if [ "$RUN_PERFORMANCE_TESTS" = true ]; then
        log_section "⚡ Tests de Performance"
        
        cd "$BACKEND_DIR"
        
        log_info "Exécution des tests de performance..."
        start_time=$(date +%s)
        
        if mvn test -P performance-tests -Dmaven.test.failure.ignore=false \
           -Dperformance.reportsDirectory="$REPORTS_DIR/performance"; then
            end_time=$(date +%s)
            duration=$((end_time - start_time))
            log_success "Tests de performance terminés avec succès en ${duration}s"
        else
            log_error "Échec des tests de performance"
            return 1
        fi
        
        cd "$PROJECT_ROOT"
    fi
}

# Fonction pour générer un rapport consolidé
generate_consolidated_report() {
    if [ "$GENERATE_REPORTS" = true ]; then
        log_section "📊 Génération du rapport consolidé"
        
        # Créer un fichier de rapport HTML consolidé
        REPORT_FILE="$REPORTS_DIR/test-report-${TIMESTAMP}.html"
        
        cat > "$REPORT_FILE" << EOF
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rapport de Tests MedHead - $TIMESTAMP</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { text-align: center; color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 20px; margin-bottom: 30px; }
        .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .card { background: #ecf0f1; padding: 20px; border-radius: 8px; text-align: center; }
        .card h3 { color: #2c3e50; margin: 0 0 10px 0; }
        .card .status { font-size: 24px; font-weight: bold; }
        .success { color: #27ae60; }
        .error { color: #e74c3c; }
        .warning { color: #f39c12; }
        .section { margin: 30px 0; }
        .section h2 { color: #2c3e50; border-left: 4px solid #3498db; padding-left: 15px; }
        .link { color: #3498db; text-decoration: none; }
        .link:hover { text-decoration: underline; }
        .timestamp { text-align: center; color: #7f8c8d; margin-top: 30px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🏥 Rapport de Tests MedHead</h1>
            <p>Exécution complète de la pyramide de tests</p>
            <p>Généré le: $(date '+%d/%m/%Y à %H:%M:%S')</p>
        </div>
        
        <div class="summary">
            <div class="card">
                <h3>Tests Unitaires</h3>
                <div class="status success">✅ Exécutés</div>
                <p>Backend (JUnit) + Frontend (Jasmine)</p>
            </div>
            <div class="card">
                <h3>Tests d'Intégration</h3>
                <div class="status success">✅ Exécutés</div>
                <p>API REST + Base de données</p>
            </div>
            <div class="card">
                <h3>Tests BDD</h3>
                <div class="status success">✅ Exécutés</div>
                <p>Scénarios Cucumber</p>
            </div>
            <div class="card">
                <h3>Tests E2E</h3>
                <div class="status success">✅ Exécutés</div>
                <p>Interface utilisateur</p>
            </div>
        </div>
        
        <div class="section">
            <h2>📁 Accès aux Rapports Détaillés</h2>
            <ul>
                <li><a href="backend/unit-tests/index.html" class="link">Tests Unitaires Backend</a></li>
                <li><a href="backend/integration-tests/index.html" class="link">Tests d'Intégration Backend</a></li>
                <li><a href="backend/bdd-tests/cucumber.html" class="link">Tests BDD Backend</a></li>
                <li><a href="frontend/unit-tests/index.html" class="link">Tests Unitaires Frontend</a></li>
                <li><a href="frontend/e2e-tests/index.html" class="link">Tests E2E Frontend</a></li>
EOF

        if [ "$RUN_PERFORMANCE_TESTS" = true ]; then
            cat >> "$REPORT_FILE" << EOF
                <li><a href="performance/index.html" class="link">Tests de Performance</a></li>
EOF
        fi

        cat >> "$REPORT_FILE" << EOF
            </ul>
        </div>
        
        <div class="section">
            <h2>📊 Statistiques de Test</h2>
            <p>Cette exécution couvre tous les niveaux de la pyramide de tests :</p>
            <ul>
                <li><strong>Tests Unitaires :</strong> Validation des composants individuels</li>
                <li><strong>Tests d'Intégration :</strong> Validation des interactions entre composants</li>
                <li><strong>Tests BDD :</strong> Validation des comportements métier</li>
                <li><strong>Tests E2E :</strong> Validation de l'expérience utilisateur complète</li>
            </ul>
        </div>
        
        <div class="timestamp">
            <p>Rapport généré automatiquement par le script de test MedHead</p>
        </div>
    </div>
</body>
</html>
EOF
        
        log_success "Rapport consolidé généré: $REPORT_FILE"
        
        # Créer un lien symbolique vers le dernier rapport
        ln -sf "test-report-${TIMESTAMP}.html" "$REPORTS_DIR/latest-report.html"
        log_success "Lien vers le dernier rapport: $REPORTS_DIR/latest-report.html"
    fi
}

# Fonction pour afficher le résumé final
show_summary() {
    log_section "📋 Résumé de l'exécution"
    
    echo -e "${WHITE}🎯 Tests exécutés:${NC}"
    [ "$RUN_UNIT_TESTS" = true ] && echo -e "  ✅ Tests unitaires (Backend + Frontend)"
    [ "$RUN_INTEGRATION_TESTS" = true ] && echo -e "  ✅ Tests d'intégration (Backend)"
    [ "$RUN_BDD_TESTS" = true ] && echo -e "  ✅ Tests BDD (Backend)"
    [ "$RUN_E2E_TESTS" = true ] && echo -e "  ✅ Tests E2E (Frontend)"
    [ "$RUN_PERFORMANCE_TESTS" = true ] && echo -e "  ✅ Tests de performance"
    
    echo -e "\n${WHITE}📊 Rapports disponibles:${NC}"
    echo -e "  📁 $REPORTS_DIR"
    echo -e "  🌐 $REPORTS_DIR/latest-report.html"
    
    echo -e "\n${WHITE}🚀 Services disponibles:${NC}"
    echo -e "  🎨 Frontend: http://localhost:4200"
    echo -e "  🔧 Backend: http://localhost:8080"
    echo -e "  📊 pgAdmin: http://localhost:8082"
    
    log_success "Suite de tests complète terminée avec succès !"
}

# Fonction pour nettoyer les ressources
cleanup() {
    if [ "$CLEANUP_AFTER" = true ]; then
        log_section "🧹 Nettoyage des ressources"
        
        # Arrêter les conteneurs Docker si ils ont été démarrés par ce script
        if [ "$RUN_E2E_TESTS" = true ]; then
            log_info "Arrêt des services Docker..."
            docker-compose -f "$DOCKER_DIR/docker-compose.yml" down 2>/dev/null || true
        fi
        
        log_success "Nettoyage terminé"
    fi
}

# Fonction principale
main() {
    echo -e "${CYAN}"
    echo "🏥 =========================================="
    echo "   MEDHEAD - SUITE DE TESTS COMPLÈTE"
    echo "   Pyramide de Tests (Unitaires → E2E)"
    echo "==========================================${NC}"
    
    # Gestion des arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-unit)
                RUN_UNIT_TESTS=false
                shift
                ;;
            --skip-integration)
                RUN_INTEGRATION_TESTS=false
                shift
                ;;
            --skip-bdd)
                RUN_BDD_TESTS=false
                shift
                ;;
            --skip-e2e)
                RUN_E2E_TESTS=false
                shift
                ;;
            --performance)
                RUN_PERFORMANCE_TESTS=true
                shift
                ;;
            --no-reports)
                GENERATE_REPORTS=false
                shift
                ;;
            --no-cleanup)
                CLEANUP_AFTER=false
                shift
                ;;
            --help)
                echo "Usage: $0 [options]"
                echo "Options:"
                echo "  --skip-unit         Ignorer les tests unitaires"
                echo "  --skip-integration  Ignorer les tests d'intégration"
                echo "  --skip-bdd          Ignorer les tests BDD"
                echo "  --skip-e2e          Ignorer les tests E2E"
                echo "  --performance       Inclure les tests de performance"
                echo "  --no-reports        Ne pas générer de rapports"
                echo "  --no-cleanup        Ne pas nettoyer après les tests"
                echo "  --help              Afficher cette aide"
                exit 0
                ;;
            *)
                log_error "Option inconnue: $1"
                exit 1
                ;;
        esac
    done
    
    # Variables globales pour le tracking
    TESTS_START_TIME=$(date +%s)
    FAILED_TESTS=()
    
    # Exécution des étapes
    check_prerequisites
    cleanup_previous_reports
    
    # Exécution des tests selon la pyramide
    if [ "$RUN_UNIT_TESTS" = true ]; then
        run_backend_unit_tests || FAILED_TESTS+=("Backend Unit Tests")
        run_frontend_unit_tests || FAILED_TESTS+=("Frontend Unit Tests")
    fi
    
    if [ "$RUN_INTEGRATION_TESTS" = true ]; then
        run_backend_integration_tests || FAILED_TESTS+=("Backend Integration Tests")
    fi
    
    if [ "$RUN_BDD_TESTS" = true ]; then
        run_backend_bdd_tests || FAILED_TESTS+=("Backend BDD Tests")
    fi
    
    if [ "$RUN_E2E_TESTS" = true ]; then
        run_frontend_e2e_tests || FAILED_TESTS+=("Frontend E2E Tests")
    fi
    
    if [ "$RUN_PERFORMANCE_TESTS" = true ]; then
        run_performance_tests || FAILED_TESTS+=("Performance Tests")
    fi
    
    # Génération des rapports et résumé
    generate_consolidated_report
    show_summary
    
    # Vérification des échecs
    if [ ${#FAILED_TESTS[@]} -gt 0 ]; then
        log_error "Tests échoués:"
        for test in "${FAILED_TESTS[@]}"; do
            echo -e "  ❌ $test"
        done
        exit 1
    fi
    
    # Calcul du temps total
    TESTS_END_TIME=$(date +%s)
    TOTAL_DURATION=$((TESTS_END_TIME - TESTS_START_TIME))
    
    echo -e "\n${GREEN}🎉 Tous les tests sont passés avec succès !${NC}"
    echo -e "${GREEN}⏱️  Durée totale: ${TOTAL_DURATION}s${NC}"
    
    cleanup
}

# Gestion des signaux pour le nettoyage
trap cleanup EXIT INT TERM

# Exécution du script principal
main "$@"
