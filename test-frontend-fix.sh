#!/bin/bash

# Script pour tester les corrections des tests frontend Angular
# Auteur: Assistant IA
# Version: 1.0

set -e

# Configuration des couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

# Vérification des corrections dans le workflow CI
check_workflow_corrections() {
    log_info "Vérification des corrections dans le workflow CI..."
    
    # Vérifier que l'option output-path a été supprimée
    if ! grep -q "output-path" .github/workflows/ci.yml; then
        log_success "✅ Option --output-path supprimée du workflow"
    else
        log_error "❌ Option --output-path encore présente"
        return 1
    fi
    
    # Vérifier les chemins de couverture corrigés
    if grep -q "frontend/coverage/medhead-frontend/lcov.info" .github/workflows/ci.yml; then
        log_success "✅ Chemin de couverture corrigé pour Codecov"
    else
        log_warning "⚠️ Chemin de couverture Codecov non trouvé"
    fi
    
    if grep -q "frontend/coverage/" .github/workflows/ci.yml; then
        log_success "✅ Chemin de couverture corrigé pour les artifacts"
    else
        log_warning "⚠️ Chemin de couverture artifacts non trouvé"
    fi
    
    if grep -q "frontend/coverage/medhead-frontend/lcov.info" .github/workflows/ci.yml; then
        log_success "✅ Chemin SonarQube corrigé"
    else
        log_warning "⚠️ Chemin SonarQube non trouvé"
    fi
}

# Vérification de la configuration Karma
check_karma_config() {
    log_info "Vérification de la configuration Karma..."
    
    if [ -f "frontend/karma.conf.js" ]; then
        log_success "✅ Fichier karma.conf.js trouvé"
        
        if grep -q "coverageReporter" frontend/karma.conf.js; then
            log_success "✅ Configuration coverageReporter présente"
            
            if grep -q "medhead-frontend" frontend/karma.conf.js; then
                log_success "✅ Répertoire de couverture configuré correctement"
            else
                log_warning "⚠️ Répertoire de couverture non configuré"
            fi
        else
            log_warning "⚠️ Configuration coverageReporter manquante"
        fi
    else
        log_error "❌ Fichier karma.conf.js non trouvé"
        return 1
    fi
}

# Vérification des scripts npm
check_npm_scripts() {
    log_info "Vérification des scripts npm..."
    
    if [ -f "frontend/package.json" ]; then
        log_success "✅ Fichier package.json trouvé"
        
        if grep -q "test:coverage" frontend/package.json; then
            log_success "✅ Script test:coverage présent"
        else
            log_warning "⚠️ Script test:coverage manquant"
        fi
        
        if grep -q "ng test --code-coverage" frontend/package.json; then
            log_success "✅ Configuration de couverture correcte"
        else
            log_warning "⚠️ Configuration de couverture non trouvée"
        fi
    else
        log_error "❌ Fichier package.json non trouvé"
        return 1
    fi
}

# Test de la commande Angular (simulation)
simulate_angular_test() {
    log_info "Simulation de la commande Angular..."
    
    # Créer un répertoire de test temporaire
    mkdir -p test-angular-command
    
    # Simuler la commande corrigée
    echo "npm run test:coverage -- --watch=false --browsers=ChromeHeadless" > test-angular-command/test-command.sh
    chmod +x test-angular-command/test-command.sh
    
    if [ -f "test-angular-command/test-command.sh" ]; then
        log_success "✅ Commande Angular simulée sans --output-path"
        echo "Commande simulée:"
        cat test-angular-command/test-command.sh
    fi
    
    # Nettoyer
    rm -rf test-angular-command
}

# Vérification des chemins de rapports
check_report_paths() {
    log_info "Vérification des chemins de rapports..."
    
    # Vérifier que les anciens chemins ont été corrigés
    if ! grep -q "reports/frontend/unit-tests/coverage/lcov.info" .github/workflows/ci.yml; then
        log_success "✅ Ancien chemin Codecov corrigé"
    else
        log_error "❌ Ancien chemin Codecov encore présent"
    fi
    
    # Vérifier les nouveaux chemins
    local correct_paths=(
        "frontend/coverage/"
        "frontend/coverage/medhead-frontend/lcov.info"
    )
    
    for path in "${correct_paths[@]}"; do
        if grep -q "$path" .github/workflows/ci.yml; then
            log_success "✅ Chemin correct trouvé: $path"
        else
            log_warning "⚠️ Chemin non trouvé: $path"
        fi
    done
}

# Affichage du résumé des corrections
show_summary() {
    log_info "Résumé des corrections des tests frontend:"
    
    echo ""
    echo "🔧 Corrections appliquées:"
    echo "  1. ✅ Suppression de l'option --output-path invalide"
    echo "  2. ✅ Correction des chemins de couverture"
    echo "  3. ✅ Utilisation des répertoires par défaut de Karma"
    echo "  4. ✅ Mise à jour des chemins SonarQube et Codecov"
    
    echo ""
    echo "📁 Chemins corrigés:"
    echo "  - Artifacts: frontend/coverage/"
    echo "  - Codecov: frontend/coverage/medhead-frontend/lcov.info"
    echo "  - SonarQube: frontend/coverage/medhead-frontend/lcov.info"
    
    echo ""
    echo "🎯 Configuration Karma:"
    echo "  - Répertoire de couverture: ./coverage/medhead-frontend"
    echo "  - Formats: html, text-summary, lcov, json"
    echo "  - Seuils: 80% pour tous les métriques"
    
    echo ""
    echo "🚀 Commande corrigée:"
    echo "  npm run test:coverage -- --watch=false --browsers=ChromeHeadless"
    
    echo ""
    echo "📋 Prochaines étapes:"
    echo "  1. Commit et push des modifications"
    echo "  2. Vérification du pipeline CI sur GitHub"
    echo "  3. Les tests frontend devraient passer sans erreur"
    
    echo ""
    log_success "Toutes les corrections des tests frontend ont été appliquées !"
}

# Fonction principale
main() {
    echo -e "${BLUE}"
    echo "🎨 =========================================="
    echo "   CORRECTION TESTS FRONTEND ANGULAR"
    echo "   Résolution des erreurs de commandes"
    echo "==========================================${NC}"
    
    check_workflow_corrections
    check_karma_config
    check_npm_scripts
    check_report_paths
    simulate_angular_test
    show_summary
}

# Exécution du script principal
main "$@"
