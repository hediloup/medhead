#!/bin/bash

# Script de configuration de l'environnement de test pour MedHead
# Installe et configure tous les outils nécessaires pour la suite de tests complète
# Auteur: Assistant IA
# Version: 1.0

set -e

# Configuration des couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuration des variables
PROJECT_ROOT="/home/hedi/projects/medhead"
BACKEND_DIR="$PROJECT_ROOT/backend"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
REPORTS_DIR="$PROJECT_ROOT/reports"

# Variables de configuration
INSTALL_DEPENDENCIES=true
CONFIGURE_ENVIRONMENT=true
CREATE_DIRECTORIES=true
SETUP_DOCKER=true
INSTALL_CYPRESS=true

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

# Fonction pour vérifier si une commande existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Fonction pour installer Java/Maven si nécessaire
setup_java_maven() {
    log_section "☕ Configuration Java/Maven"
    
    if ! command_exists java; then
        log_info "Installation de Java..."
        sudo apt-get update
        sudo apt-get install -y openjdk-17-jdk
        log_success "Java installé: $(java -version 2>&1 | head -n1)"
    else
        log_success "Java déjà installé: $(java -version 2>&1 | head -n1)"
    fi
    
    if ! command_exists mvn; then
        log_info "Installation de Maven..."
        sudo apt-get install -y maven
        log_success "Maven installé: $(mvn -version | head -n1)"
    else
        log_success "Maven déjà installé: $(mvn -version | head -n1)"
    fi
    
    # Configuration des variables d'environnement Java
    if ! grep -q "JAVA_HOME" ~/.bashrc; then
        log_info "Configuration de JAVA_HOME..."
        echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
        echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
        source ~/.bashrc
        log_success "JAVA_HOME configuré"
    fi
}

# Fonction pour installer Node.js/npm si nécessaire
setup_nodejs() {
    log_section "📦 Configuration Node.js/npm"
    
    if ! command_exists node; then
        log_info "Installation de Node.js..."
        curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
        sudo apt-get install -y nodejs
        log_success "Node.js installé: $(node --version)"
    else
        log_success "Node.js déjà installé: $(node --version)"
    fi
    
    if ! command_exists npm; then
        log_error "npm n'est pas disponible avec Node.js"
        exit 1
    else
        log_success "npm disponible: $(npm --version)"
    fi
}

# Fonction pour installer Docker si nécessaire
setup_docker() {
    if [ "$SETUP_DOCKER" = true ]; then
        log_section "🐳 Configuration Docker"
        
        if ! command_exists docker; then
            log_info "Installation de Docker..."
            curl -fsSL https://get.docker.com -o get-docker.sh
            sudo sh get-docker.sh
            sudo usermod -aG docker $USER
            rm get-docker.sh
            log_success "Docker installé: $(docker --version)"
            log_warning "Veuillez redémarrer votre session pour que les permissions Docker prennent effet"
        else
            log_success "Docker déjà installé: $(docker --version)"
        fi
        
        if ! command_exists docker-compose; then
            log_info "Installation de Docker Compose..."
            sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
            sudo chmod +x /usr/local/bin/docker-compose
            log_success "Docker Compose installé: $(docker-compose --version)"
        else
            log_success "Docker Compose déjà installé: $(docker-compose --version)"
        fi
    fi
}

# Fonction pour créer les répertoires nécessaires
create_test_directories() {
    if [ "$CREATE_DIRECTORIES" = true ]; then
        log_section "📁 Création des répertoires de test"
        
        directories=(
            "$REPORTS_DIR"
            "$REPORTS_DIR/backend"
            "$REPORTS_DIR/backend/unit-tests"
            "$REPORTS_DIR/backend/integration-tests"
            "$REPORTS_DIR/backend/bdd-tests"
            "$REPORTS_DIR/frontend"
            "$REPORTS_DIR/frontend/unit-tests"
            "$REPORTS_DIR/frontend/e2e-tests"
            "$REPORTS_DIR/performance"
            "$REPORTS_DIR/consolidated"
        )
        
        for dir in "${directories[@]}"; do
            mkdir -p "$dir"
            log_success "Répertoire créé: $dir"
        done
    fi
}

# Fonction pour installer les dépendances Backend
install_backend_dependencies() {
    log_section "🔧 Installation des dépendances Backend"
    
    cd "$BACKEND_DIR"
    
    log_info "Installation des dépendances Maven..."
    if mvn clean install -DskipTests; then
        log_success "Dépendances Backend installées avec succès"
    else
        log_error "Échec de l'installation des dépendances Backend"
        exit 1
    fi
    
    cd "$PROJECT_ROOT"
}

# Fonction pour installer les dépendances Frontend
install_frontend_dependencies() {
    log_section "🎨 Installation des dépendances Frontend"
    
    cd "$FRONTEND_DIR"
    
    log_info "Installation des dépendances npm..."
    if npm install; then
        log_success "Dépendances Frontend installées avec succès"
    else
        log_error "Échec de l'installation des dépendances Frontend"
        exit 1
    fi
    
    cd "$PROJECT_ROOT"
}

# Fonction pour installer Cypress
setup_cypress() {
    if [ "$INSTALL_CYPRESS" = true ]; then
        log_section "🌐 Configuration Cypress"
        
        cd "$FRONTEND_DIR"
        
        if [ ! -d "node_modules/cypress" ]; then
            log_info "Installation de Cypress..."
            if npm install cypress --save-dev; then
                log_success "Cypress installé avec succès"
            else
                log_error "Échec de l'installation de Cypress"
                exit 1
            fi
        else
            log_success "Cypress déjà installé"
        fi
        
        # Configuration initiale de Cypress
        log_info "Configuration initiale de Cypress..."
        if npx cypress install; then
            log_success "Cypress configuré avec succès"
        else
            log_warning "Configuration Cypress échouée, mais cela peut être normal"
        fi
        
        cd "$PROJECT_ROOT"
    fi
}

# Fonction pour configurer les variables d'environnement
setup_environment_variables() {
    log_section "⚙️ Configuration des variables d'environnement"
    
    env_file="$PROJECT_ROOT/.env.test"
    
    cat > "$env_file" << EOF
# Configuration de test pour MedHead
SPRING_PROFILES_ACTIVE=test
CUCUMBER_OPTIONS=--plugin pretty --plugin html:target/cucumber-reports
CYPRESS_BASE_URL=http://localhost:4200
CYPRESS_REPORTS_DIR=$REPORTS_DIR/frontend/e2e-tests
MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=256m
NODE_OPTIONS=--max-old-space-size=4096
EOF
    
    log_success "Variables d'environnement configurées dans $env_file"
    
    # Ajouter le sourcing du fichier .env dans .bashrc si pas déjà fait
    if ! grep -q "source.*\.env\.test" ~/.bashrc; then
        echo "source $env_file" >> ~/.bashrc
        log_success "Variables d'environnement ajoutées au .bashrc"
    fi
}

# Fonction pour créer un fichier de configuration de test
create_test_configuration() {
    log_section "📋 Création de la configuration de test"
    
    # Configuration pour les tests Backend
    backend_test_config="$BACKEND_DIR/src/test/resources/application-test.properties"
    
    cat > "$backend_test_config" << EOF
# Configuration de test pour le backend MedHead
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Configuration pour les tests de performance
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Désactiver la sécurité pour les tests
spring.security.user.name=test
spring.security.user.password=test

# Configuration Actuator pour les tests
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

# Configuration des logs de test
logging.level.com.medhead=DEBUG
logging.level.org.springframework.test=DEBUG
logging.level.io.cucumber=INFO
EOF
    
    log_success "Configuration de test Backend créée"
    
    # Configuration pour Cypress
    cypress_config="$FRONTEND_DIR/cypress.config.js"
    
    if [ -f "$cypress_config" ]; then
        log_success "Configuration Cypress déjà présente"
    else
        cat > "$cypress_config" << EOF
const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    specPattern: 'cypress/e2e/**/*.cy.js',
    screenshotsFolder: 'reports/screenshots',
    videosFolder: 'reports/videos',
    supportFile: 'cypress/support/e2e.js',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: true,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    pageLoadTimeout: 30000,
    retries: {
      runMode: 2,
      openMode: 0
    }
  }
});
EOF
        log_success "Configuration Cypress créée"
    fi
}

# Fonction pour valider l'installation
validate_installation() {
    log_section "✅ Validation de l'installation"
    
    local validation_passed=true
    
    # Vérifier Java
    if command_exists java && command_exists mvn; then
        log_success "✅ Java/Maven: OK"
    else
        log_error "❌ Java/Maven: Manquant"
        validation_passed=false
    fi
    
    # Vérifier Node.js
    if command_exists node && command_exists npm; then
        log_success "✅ Node.js/npm: OK"
    else
        log_error "❌ Node.js/npm: Manquant"
        validation_passed=false
    fi
    
    # Vérifier Docker
    if command_exists docker && command_exists docker-compose; then
        log_success "✅ Docker: OK"
    else
        log_warning "⚠️  Docker: Manquant (optionnel pour certains tests)"
    fi
    
    # Vérifier les répertoires
    if [ -d "$REPORTS_DIR" ]; then
        log_success "✅ Répertoires de test: OK"
    else
        log_error "❌ Répertoires de test: Manquants"
        validation_passed=false
    fi
    
    # Vérifier les dépendances Backend
    if [ -d "$BACKEND_DIR/target" ]; then
        log_success "✅ Dépendances Backend: OK"
    else
        log_error "❌ Dépendances Backend: Manquantes"
        validation_passed=false
    fi
    
    # Vérifier les dépendances Frontend
    if [ -d "$FRONTEND_DIR/node_modules" ]; then
        log_success "✅ Dépendances Frontend: OK"
    else
        log_error "❌ Dépendances Frontend: Manquantes"
        validation_passed=false
    fi
    
    # Vérifier Cypress
    if [ -d "$FRONTEND_DIR/node_modules/cypress" ]; then
        log_success "✅ Cypress: OK"
    else
        log_error "❌ Cypress: Manquant"
        validation_passed=false
    fi
    
    if [ "$validation_passed" = true ]; then
        log_success "🎉 Installation validée avec succès !"
        return 0
    else
        log_error "❌ Installation incomplète. Veuillez corriger les erreurs ci-dessus."
        return 1
    fi
}

# Fonction pour afficher les instructions post-installation
show_post_installation_instructions() {
    log_section "📖 Instructions post-installation"
    
    echo -e "${CYAN}🎯 Prochaines étapes:${NC}"
    echo -e "1. ${YELLOW}Redémarrez votre terminal${NC} pour que les variables d'environnement prennent effet"
    echo -e "2. ${YELLOW}Ajoutez votre utilisateur au groupe docker${NC} (si Docker a été installé):"
    echo -e "   ${BLUE}sudo usermod -aG docker \$USER${NC}"
    echo -e "   ${BLUE}newgrp docker${NC}"
    echo -e "3. ${YELLOW}Exécutez la suite de tests complète:${NC}"
    echo -e "   ${BLUE}./run-complete-test-suite.sh${NC}"
    echo -e "4. ${YELLOW}Consultez les rapports de test:${NC}"
    echo -e "   ${BLUE}$REPORTS_DIR/latest-report.html${NC}"
    
    echo -e "\n${CYAN}🔧 Commandes utiles:${NC}"
    echo -e "• ${BLUE}./run-complete-test-suite.sh --help${NC} - Aide du script de test"
    echo -e "• ${BLUE}./run-complete-test-suite.sh --skip-e2e${NC} - Exclure les tests E2E"
    echo -e "• ${BLUE}./run-complete-test-suite.sh --performance${NC} - Inclure les tests de performance"
    echo -e "• ${BLUE}mvn test${NC} - Tests Backend uniquement"
    echo -e "• ${BLUE}npm test${NC} - Tests Frontend uniquement"
    echo -e "• ${BLUE}npm run e2e${NC} - Tests E2E uniquement"
    
    echo -e "\n${CYAN}📊 Rapports disponibles:${NC}"
    echo -e "• ${BLUE}$REPORTS_DIR/backend/unit-tests/${NC} - Tests unitaires Backend"
    echo -e "• ${BLUE}$REPORTS_DIR/backend/integration-tests/${NC} - Tests d'intégration Backend"
    echo -e "• ${BLUE}$REPORTS_DIR/backend/bdd-tests/${NC} - Tests BDD Backend"
    echo -e "• ${BLUE}$REPORTS_DIR/frontend/unit-tests/${NC} - Tests unitaires Frontend"
    echo -e "• ${BLUE}$REPORTS_DIR/frontend/e2e-tests/${NC} - Tests E2E Frontend"
}

# Fonction principale
main() {
    echo -e "${CYAN}"
    echo "🏥 =========================================="
    echo "   MEDHEAD - CONFIGURATION ENVIRONNEMENT"
    echo "   DE TEST COMPLET"
    echo "==========================================${NC}"
    
    # Gestion des arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-docker)
                SETUP_DOCKER=false
                shift
                ;;
            --skip-cypress)
                INSTALL_CYPRESS=false
                shift
                ;;
            --help)
                echo "Usage: $0 [options]"
                echo "Options:"
                echo "  --skip-docker     Ne pas installer Docker"
                echo "  --skip-cypress    Ne pas installer Cypress"
                echo "  --help            Afficher cette aide"
                exit 0
                ;;
            *)
                log_error "Option inconnue: $1"
                exit 1
                ;;
        esac
    done
    
    # Exécution des étapes d'installation
    setup_java_maven
    setup_nodejs
    setup_docker
    create_test_directories
    install_backend_dependencies
    install_frontend_dependencies
    setup_cypress
    setup_environment_variables
    create_test_configuration
    
    # Validation et instructions finales
    if validate_installation; then
        show_post_installation_instructions
        log_success "🎉 Configuration de l'environnement de test terminée avec succès !"
        exit 0
    else
        log_error "❌ Configuration incomplète. Veuillez corriger les erreurs."
        exit 1
    fi
}

# Exécution du script principal
main "$@"
