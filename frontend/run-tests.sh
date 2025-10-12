#!/bin/bash

# Script pour exécuter tous les tests du frontend MedHead
# Usage: ./run-tests.sh [unit|e2e|all|coverage]

set -e

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages colorés
print_message() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Fonction pour vérifier si les dépendances sont installées
check_dependencies() {
    print_message "Vérification des dépendances..."
    
    if [ ! -d "node_modules" ]; then
        print_warning "node_modules non trouvé. Installation des dépendances..."
        npm install
    fi
    
    if ! command -v ng &> /dev/null; then
        print_error "Angular CLI non trouvé. Installation..."
        npm install -g @angular/cli
    fi
}

# Fonction pour exécuter les tests unitaires
run_unit_tests() {
    print_message "Exécution des tests unitaires..."
    
    # Vérifier si le serveur de test est déjà en cours d'exécution
    if pgrep -f "ng test" > /dev/null; then
        print_warning "Tests unitaires déjà en cours d'exécution"
        return 0
    fi
    
    # Exécuter les tests unitaires
    if npm run test -- --watch=false --browsers=ChromeHeadless; then
        print_success "Tests unitaires réussis"
    else
        print_error "Tests unitaires échoués"
        return 1
    fi
}

# Fonction pour exécuter les tests unitaires avec couverture
run_unit_tests_with_coverage() {
    print_message "Exécution des tests unitaires avec couverture..."
    
    if npm run test:coverage -- --watch=false --browsers=ChromeHeadless; then
        print_success "Tests unitaires avec couverture réussis"
        
        # Ouvrir le rapport de couverture si disponible
        if [ -f "coverage/index.html" ]; then
            print_message "Rapport de couverture disponible dans coverage/index.html"
            if command -v xdg-open &> /dev/null; then
                xdg-open coverage/index.html
            elif command -v open &> /dev/null; then
                open coverage/index.html
            fi
        fi
    else
        print_error "Tests unitaires avec couverture échoués"
        return 1
    fi
}

# Fonction pour exécuter les tests E2E
run_e2e_tests() {
    print_message "Exécution des tests E2E..."
    
    # Vérifier si l'application est en cours d'exécution
    if ! curl -s http://localhost:4200 > /dev/null; then
        print_warning "Application non disponible sur localhost:4200"
        print_message "Démarrage de l'application en arrière-plan..."
        
        # Démarrer l'application en arrière-plan
        npm run start &
        APP_PID=$!
        
        # Attendre que l'application soit prête
        print_message "Attente du démarrage de l'application..."
        for i in {1..30}; do
            if curl -s http://localhost:4200 > /dev/null; then
                print_success "Application démarrée avec succès"
                break
            fi
            sleep 2
        done
        
        if ! curl -s http://localhost:4200 > /dev/null; then
            print_error "Impossible de démarrer l'application"
            kill $APP_PID 2>/dev/null || true
            return 1
        fi
    fi
    
    # Exécuter les tests E2E
    if npm run e2e:headless; then
        print_success "Tests E2E réussis"
        
        # Nettoyer les processus en arrière-plan
        if [ ! -z "$APP_PID" ]; then
            kill $APP_PID 2>/dev/null || true
        fi
    else
        print_error "Tests E2E échoués"
        
        # Nettoyer les processus en arrière-plan
        if [ ! -z "$APP_PID" ]; then
            kill $APP_PID 2>/dev/null || true
        fi
        return 1
    fi
}

# Fonction pour exécuter tous les tests
run_all_tests() {
    print_message "Exécution de tous les tests..."
    
    local failed=0
    
    # Tests unitaires
    if ! run_unit_tests; then
        failed=1
    fi
    
    echo ""
    
    # Tests E2E
    if ! run_e2e_tests; then
        failed=1
    fi
    
    if [ $failed -eq 0 ]; then
        print_success "Tous les tests sont réussis !"
    else
        print_error "Certains tests ont échoué"
        return 1
    fi
}

# Fonction pour afficher l'aide
show_help() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commandes disponibles:"
    echo "  unit      Exécuter les tests unitaires"
    echo "  e2e       Exécuter les tests E2E"
    echo "  coverage  Exécuter les tests unitaires avec couverture de code"
    echo "  all       Exécuter tous les tests (unitaire + E2E)"
    echo "  help      Afficher cette aide"
    echo ""
    echo "Exemples:"
    echo "  $0 unit          # Tests unitaires seulement"
    echo "  $0 e2e           # Tests E2E seulement"
    echo "  $0 coverage      # Tests unitaires avec couverture"
    echo "  $0 all           # Tous les tests"
}

# Fonction principale
main() {
    # Vérifier les dépendances
    check_dependencies
    
    # Analyser les arguments
    case "${1:-all}" in
        "unit")
            run_unit_tests
            ;;
        "e2e")
            run_e2e_tests
            ;;
        "coverage")
            run_unit_tests_with_coverage
            ;;
        "all")
            run_all_tests
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            print_error "Commande inconnue: $1"
            show_help
            exit 1
            ;;
    esac
}

# Point d'entrée
main "$@"
