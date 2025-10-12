#!/bin/bash

# Script pour exécuter les tests frontend avec Docker
# Usage: ./test-frontend.sh [unit|e2e|all|dev]

set -e

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_message() {
    echo -e "${BLUE}[DOCKER]${NC} $1"
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

# Fonction pour nettoyer les conteneurs
cleanup() {
    print_message "Nettoyage des conteneurs..."
    docker-compose down --remove-orphans 2>/dev/null || true
    docker system prune -f 2>/dev/null || true
}

# Fonction pour construire l'image de test
build_test_image() {
    print_message "Construction de l'image de test frontend..."
    if docker-compose build frontend-unit-tests; then
        print_success "Image de test construite avec succès"
    else
        print_error "Échec de la construction de l'image de test"
        exit 1
    fi
}

# Fonction pour exécuter les tests unitaires
run_unit_tests() {
    print_message "Exécution des tests unitaires avec Docker..."
    
    if docker-compose --profile test up frontend-unit-tests; then
        print_success "Tests unitaires réussis"
        
        # Copier les rapports de couverture
        if [ -d "../frontend/reports" ]; then
            print_message "Rapports de couverture disponibles dans frontend/reports/"
        fi
    else
        print_error "Tests unitaires échoués"
        return 1
    fi
}

# Fonction pour exécuter les tests E2E
run_e2e_tests() {
    print_message "Exécution des tests E2E avec Docker..."
    
    # Démarrer l'application frontend d'abord
    print_message "Démarrage de l'application frontend..."
    docker-compose --profile dev up -d frontend-dev
    
    # Attendre que l'application soit prête
    print_message "Attente du démarrage de l'application..."
    for i in {1..30}; do
        if curl -s http://localhost:4200 > /dev/null; then
            print_success "Application frontend démarrée"
            break
        fi
        sleep 2
    done
    
    # Exécuter les tests E2E
    if docker-compose --profile test up frontend-e2e-tests; then
        print_success "Tests E2E réussis"
    else
        print_error "Tests E2E échoués"
        return 1
    fi
}

# Fonction pour exécuter tous les tests
run_all_tests() {
    print_message "Exécution de tous les tests avec Docker..."
    
    # Démarrer l'application frontend
    print_message "Démarrage de l'application frontend..."
    docker-compose --profile dev up -d frontend-dev
    
    # Attendre que l'application soit prête
    print_message "Attente du démarrage de l'application..."
    for i in {1..30}; do
        if curl -s http://localhost:4200 > /dev/null; then
            print_success "Application frontend démarrée"
            break
        fi
        sleep 2
    done
    
    # Exécuter tous les tests
    if docker-compose --profile test up frontend-all-tests; then
        print_success "Tous les tests sont réussis"
    else
        print_error "Certains tests ont échoué"
        return 1
    fi
}

# Fonction pour démarrer l'environnement de développement
start_dev() {
    print_message "Démarrage de l'environnement de développement..."
    
    if docker-compose --profile dev up -d frontend-dev; then
        print_success "Environnement de développement démarré"
        print_message "Application disponible sur http://localhost:4200"
        print_message "Pour arrêter: docker-compose down"
    else
        print_error "Échec du démarrage de l'environnement de développement"
        return 1
    fi
}

# Fonction pour afficher l'aide
show_help() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commandes disponibles:"
    echo "  unit      Exécuter les tests unitaires avec Docker"
    echo "  e2e       Exécuter les tests E2E avec Docker"
    echo "  all       Exécuter tous les tests avec Docker"
    echo "  dev       Démarrer l'environnement de développement"
    echo "  build     Construire l'image de test"
    echo "  clean     Nettoyer les conteneurs et images"
    echo "  help      Afficher cette aide"
    echo ""
    echo "Exemples:"
    echo "  $0 build          # Construire l'image de test"
    echo "  $0 unit           # Tests unitaires seulement"
    echo "  $0 e2e            # Tests E2E seulement"
    echo "  $0 all            # Tous les tests"
    echo "  $0 dev            # Environnement de développement"
    echo "  $0 clean          # Nettoyage"
}

# Fonction principale
main() {
    # Vérifier que Docker est disponible
    if ! command -v docker &> /dev/null; then
        print_error "Docker n'est pas installé ou n'est pas disponible"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose n'est pas installé ou n'est pas disponible"
        exit 1
    fi
    
    # Analyser les arguments
    case "${1:-help}" in
        "unit")
            build_test_image
            run_unit_tests
            ;;
        "e2e")
            build_test_image
            run_e2e_tests
            ;;
        "all")
            build_test_image
            run_all_tests
            ;;
        "dev")
            start_dev
            ;;
        "build")
            build_test_image
            ;;
        "clean")
            cleanup
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

# Gestion des signaux pour nettoyer en cas d'interruption
trap cleanup EXIT INT TERM

# Point d'entrée
main "$@"
