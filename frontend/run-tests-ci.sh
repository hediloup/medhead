#!/bin/bash

# Script pour exécuter les tests en mode CI/CD
# Usage: ./run-tests-ci.sh

set -e

# Configuration pour CI/CD
export CHROME_BIN=/usr/bin/google-chrome-stable
export DISPLAY=:99.0

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_message() {
    echo -e "${BLUE}[CI]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[CI SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[CI WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[CI ERROR]${NC} $1"
}

# Fonction pour installer les dépendances
install_dependencies() {
    print_message "Installation des dépendances..."
    npm ci --only=production
    npm install
}

# Fonction pour construire l'application
build_application() {
    print_message "Construction de l'application..."
    if npm run build; then
        print_success "Application construite avec succès"
    else
        print_error "Échec de la construction de l'application"
        exit 1
    fi
}

# Fonction pour exécuter les tests unitaires en mode CI
run_unit_tests_ci() {
    print_message "Exécution des tests unitaires en mode CI..."
    
    if npm run test -- --watch=false --browsers=ChromeHeadless --code-coverage --reporters=coverage-istanbul; then
        print_success "Tests unitaires réussis"
        
        # Générer le rapport de couverture
        if [ -d "coverage" ]; then
            print_message "Rapport de couverture généré dans coverage/"
            
            # Afficher le résumé de couverture
            if [ -f "coverage/lcov-report/index.html" ]; then
                print_message "Rapport de couverture HTML disponible"
            fi
        fi
    else
        print_error "Tests unitaires échoués"
        exit 1
    fi
}

# Fonction pour exécuter les tests E2E en mode CI
run_e2e_tests_ci() {
    print_message "Exécution des tests E2E en mode CI..."
    
    # Démarrer l'application en arrière-plan
    print_message "Démarrage de l'application pour les tests E2E..."
    npm run start &
    APP_PID=$!
    
    # Attendre que l'application soit prête
    print_message "Attente du démarrage de l'application..."
    for i in {1..60}; do
        if curl -s http://localhost:4200 > /dev/null; then
            print_success "Application démarrée"
            break
        fi
        sleep 2
    done
    
    if ! curl -s http://localhost:4200 > /dev/null; then
        print_error "Impossible de démarrer l'application pour les tests E2E"
        kill $APP_PID 2>/dev/null || true
        exit 1
    fi
    
    # Exécuter les tests E2E
    if npm run e2e:ci; then
        print_success "Tests E2E réussis"
    else
        print_error "Tests E2E échoués"
        kill $APP_PID 2>/dev/null || true
        exit 1
    fi
    
    # Nettoyer
    kill $APP_PID 2>/dev/null || true
}

# Fonction pour vérifier la qualité du code
check_code_quality() {
    print_message "Vérification de la qualité du code..."
    
    # Vérifier les erreurs TypeScript
    if npx tsc --noEmit; then
        print_success "Aucune erreur TypeScript"
    else
        print_error "Erreurs TypeScript détectées"
        exit 1
    fi
    
    # Vérifier le linting (si configuré)
    if [ -f ".eslintrc.json" ] || [ -f ".eslintrc.js" ]; then
        if npx eslint src/ --ext .ts,.js; then
            print_success "Linting réussi"
        else
            print_warning "Problèmes de linting détectés"
        fi
    fi
}

# Fonction pour générer les rapports
generate_reports() {
    print_message "Génération des rapports..."
    
    # Créer le dossier reports s'il n'existe pas
    mkdir -p reports
    
    # Copier les rapports de couverture
    if [ -d "coverage" ]; then
        cp -r coverage reports/unit-coverage
        print_message "Rapport de couverture unitaire copié vers reports/unit-coverage/"
    fi
    
    # Copier les rapports E2E
    if [ -d "reports/screenshots" ]; then
        print_message "Screenshots E2E disponibles dans reports/screenshots/"
    fi
    
    if [ -d "reports/videos" ]; then
        print_message "Vidéos E2E disponibles dans reports/videos/"
    fi
}

# Fonction pour nettoyer
cleanup() {
    print_message "Nettoyage..."
    
    # Tuer tous les processus Angular
    pkill -f "ng serve" 2>/dev/null || true
    pkill -f "ng test" 2>/dev/null || true
    
    # Nettoyer les fichiers temporaires
    rm -rf .angular/cache 2>/dev/null || true
}

# Fonction pour afficher le résumé
show_summary() {
    print_message "Résumé des tests CI/CD:"
    echo "✅ Construction de l'application"
    echo "✅ Tests unitaires avec couverture"
    echo "✅ Tests E2E"
    echo "✅ Vérification de la qualité du code"
    echo "✅ Génération des rapports"
    
    print_success "Tous les tests CI/CD sont réussis !"
}

# Fonction principale
main() {
    print_message "Démarrage des tests CI/CD pour MedHead Frontend"
    
    # Nettoyer avant de commencer
    cleanup
    
    # Installer les dépendances
    install_dependencies
    
    # Vérifier la qualité du code
    check_code_quality
    
    # Construire l'application
    build_application
    
    # Exécuter les tests unitaires
    run_unit_tests_ci
    
    # Exécuter les tests E2E
    run_e2e_tests_ci
    
    # Générer les rapports
    generate_reports
    
    # Afficher le résumé
    show_summary
    
    print_success "Pipeline CI/CD terminé avec succès !"
}

# Gestion des signaux pour nettoyer en cas d'interruption
trap cleanup EXIT INT TERM

# Point d'entrée
main "$@"
