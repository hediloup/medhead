#!/bin/bash

# Script de test pour le projet MedHead
# Usage: ./run-tests.sh [type]
# Types disponibles: unit, bdd, all, clean

set -e

echo "🏥 MedHead - Script de Tests"
echo "=============================="

case "${1:-all}" in
    "unit")
        echo "🧪 Exécution des tests unitaires..."
        ./mvnw clean test
        echo "✅ Tests unitaires terminés avec succès"
        ;;
    "bdd")
        echo "🎭 Exécution des tests BDD..."
        ./mvnw clean test -Pbdd-tests
        echo "✅ Tests BDD terminés avec succès"
        ;;
    "all")
        echo "🚀 Exécution de tous les tests..."
        ./mvnw clean test
        echo "✅ Tous les tests terminés avec succès"
        ;;
    "clean")
        echo "🧹 Nettoyage des fichiers de test..."
        ./mvnw clean
        rm -rf target/
        echo "✅ Nettoyage terminé"
        ;;
    "help")
        echo "Usage: $0 [type]"
        echo ""
        echo "Types disponibles:"
        echo "  unit  - Tests unitaires uniquement (recommandé)"
        echo "  bdd   - Tests BDD Cucumber uniquement"
        echo "  all   - Tous les tests (par défaut)"
        echo "  clean - Nettoyage des fichiers de test"
        echo "  help  - Affiche cette aide"
        ;;
    *)
        echo "❌ Type de test inconnu: $1"
        echo "Utilisez '$0 help' pour voir les options disponibles"
        exit 1
        ;;
esac
