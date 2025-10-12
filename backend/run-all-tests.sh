#!/bin/bash

# Script pour exécuter tous les types de tests du projet MedHead
# Approches TDD et BDD combinées

echo "🏥 === Tests MedHead Backend ==="
echo "📅 $(date)"
echo ""

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

# Fonction pour exécuter les tests avec gestion d'erreur
run_tests() {
    local test_type=$1
    local maven_profile=$2
    local description=$3
    
    print_message "Exécution des $description..."
    
    if [ -n "$maven_profile" ]; then
        mvn test -P $maven_profile
    else
        mvn test
    fi
    
    if [ $? -eq 0 ]; then
        print_success "$description terminés avec succès"
        return 0
    else
        print_error "$description échoués"
        return 1
    fi
}

# Vérifier que Maven est installé
if ! command -v mvn &> /dev/null; then
    print_error "Maven n'est pas installé ou n'est pas dans le PATH"
    exit 1
fi

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    print_error "Ce script doit être exécuté depuis le répertoire backend du projet"
    exit 1
fi

# Menu interactif
echo "Choisissez le type de tests à exécuter :"
echo "1) Tests TDD unitaires uniquement"
echo "2) Tests TDD d'intégration uniquement"
echo "3) Tests BDD uniquement"
echo "4) Tests de performance uniquement"
echo "5) Tous les tests TDD (unitaires + intégration)"
echo "6) Tous les tests (TDD + BDD)"
echo "7) Suite complète (TDD + BDD + Performance)"
echo "8) Tests par défaut (TDD unitaires + intégration)"
echo "9) Quitter"
echo ""

read -p "Votre choix (1-9): " choice

case $choice in
    1)
        print_message "=== Exécution des tests TDD unitaires ==="
        run_tests "unit" "unit-tests" "Tests TDD unitaires"
        ;;
    2)
        print_message "=== Exécution des tests TDD d'intégration ==="
        run_tests "integration" "integration-tests" "Tests TDD d'intégration"
        ;;
    3)
        print_message "=== Exécution des tests BDD ==="
        run_tests "bdd" "bdd-tests" "Tests BDD"
        ;;
    4)
        print_message "=== Exécution des tests de performance ==="
        run_tests "performance" "performance-tests" "Tests de performance"
        ;;
    5)
        print_message "=== Exécution de tous les tests TDD ==="
        run_tests "tdd" "" "Tests TDD (unitaires + intégration)"
        ;;
    6)
        print_message "=== Exécution de tous les tests (TDD + BDD) ==="
        run_tests "all" "all-tests" "Tests TDD + BDD"
        ;;
    7)
        print_message "=== Exécution de la suite complète ==="
        
        # Tests TDD
        print_message "Phase 1: Tests TDD unitaires..."
        if run_tests "unit" "unit-tests" "Tests TDD unitaires"; then
            print_message "Phase 2: Tests TDD d'intégration..."
            if run_tests "integration" "integration-tests" "Tests TDD d'intégration"; then
                print_message "Phase 3: Tests BDD..."
                if run_tests "bdd" "bdd-tests" "Tests BDD"; then
                    print_message "Phase 4: Tests de performance..."
                    run_tests "performance" "performance-tests" "Tests de performance"
                else
                    print_error "Les tests BDD ont échoué, arrêt de la suite complète"
                    exit 1
                fi
            else
                print_error "Les tests d'intégration ont échoué, arrêt de la suite complète"
                exit 1
            fi
        else
            print_error "Les tests unitaires ont échoué, arrêt de la suite complète"
            exit 1
        fi
        
        print_success "Suite complète terminée avec succès !"
        ;;
    8)
        print_message "=== Exécution des tests par défaut ==="
        run_tests "default" "" "Tests par défaut (TDD unitaires + intégration)"
        ;;
    9)
        print_message "Au revoir !"
        exit 0
        ;;
    *)
        print_error "Choix invalide"
        exit 1
        ;;
esac

# Afficher les rapports générés
echo ""
print_message "Rapports de tests générés :"
if [ -d "target/surefire-reports" ]; then
    echo "  📊 Rapports Surefire: target/surefire-reports/"
fi
if [ -d "target/cucumber-reports" ]; then
    echo "  🥒 Rapports Cucumber: target/cucumber-reports/"
fi
if [ -d "target/failsafe-reports" ]; then
    echo "  🔒 Rapports Failsafe: target/failsafe-reports/"
fi

echo ""
print_message "Pour voir les rapports HTML, ouvrez :"
echo "  - Tests unitaires: target/surefire-reports/index.html"
echo "  - Tests BDD: target/cucumber-reports/cucumber.html"
echo "  - Tests d'intégration: target/failsafe-reports/index.html"

echo ""
print_success "Exécution terminée !"
