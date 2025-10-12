#!/bin/bash

echo "🧪 Test des corrections CI/CD pour MedHead"
echo "============================================="

# Fonction pour tester les commandes
test_command() {
    local description="$1"
    local command="$2"
    local working_dir="$3"
    
    echo ""
    echo "🔍 Test: $description"
    echo "📁 Répertoire: $working_dir"
    echo "⚡ Commande: $command"
    echo "----------------------------------------"
    
    if [ -n "$working_dir" ]; then
        cd "$working_dir" || exit 1
    fi
    
    if eval "$command"; then
        echo "✅ SUCCÈS: $description"
        return 0
    else
        echo "❌ ÉCHEC: $description"
        return 1
    fi
}

# Variables
BACKEND_DIR="backend"
FRONTEND_DIR="frontend"
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Début des tests..."

# Test 1: Vérification du profil unit-tests Maven
test_command "Vérification du profil unit-tests Maven" "mvn help:active-profiles -P unit-tests" "$BACKEND_DIR"
if [ $? -eq 0 ]; then
    ((SUCCESS_COUNT++))
fi
((TOTAL_TESTS++))

# Test 2: Compilation du backend
test_command "Compilation du backend" "mvn clean compile -DskipTests" "$BACKEND_DIR"
if [ $? -eq 0 ]; then
    ((SUCCESS_COUNT++))
fi
((TOTAL_TESTS++))

# Test 3: Tests unitaires backend (si des tests existent)
if [ -d "$BACKEND_DIR/src/test/java" ] && [ "$(find "$BACKEND_DIR/src/test/java" -name "*Test*.java" | wc -l)" -gt 0 ]; then
    test_command "Tests unitaires backend" "mvn test -P unit-tests -Dspring.profiles.active=test" "$BACKEND_DIR"
    if [ $? -eq 0 ]; then
        ((SUCCESS_COUNT++))
    fi
else
    echo "⚠️ Aucun test unitaire trouvé dans le backend"
fi
((TOTAL_TESTS++))

# Test 4: Installation des dépendances frontend
test_command "Installation des dépendances frontend" "npm ci" "$FRONTEND_DIR"
if [ $? -eq 0 ]; then
    ((SUCCESS_COUNT++))
fi
((TOTAL_TESTS++))

# Test 5: Tests frontend (si des tests existent)
if [ -d "$FRONTEND_DIR/src" ] && [ "$(find "$FRONTEND_DIR/src" -name "*.spec.ts" | wc -l)" -gt 0 ]; then
    test_command "Tests unitaires frontend" "npm run test:coverage -- --watch=false --browsers=ChromeHeadless --reporters=html,coverage,junit" "$FRONTEND_DIR"
    if [ $? -eq 0 ]; then
        ((SUCCESS_COUNT++))
    fi
else
    echo "⚠️ Aucun test frontend trouvé"
fi
((TOTAL_TESTS++))

# Test 6: Build frontend
test_command "Build frontend" "npm run build --configuration=production" "$FRONTEND_DIR"
if [ $? -eq 0 ]; then
    ((SUCCESS_COUNT++))
fi
((TOTAL_TESTS++))

# Test 7: Vérification des rapports JaCoCo
test_command "Génération rapport JaCoCo" "mvn jacoco:report" "$BACKEND_DIR"
if [ $? -eq 0 ]; then
    ((SUCCESS_COUNT++))
fi
((TOTAL_TESTS++))

# Résumé des tests
echo ""
echo "📊 RÉSUMÉ DES TESTS"
echo "==================="
echo "Tests réussis: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 TOUS LES TESTS SONT PASSÉS !"
    echo "✅ Les corrections CI/CD sont fonctionnelles"
    exit 0
else
    echo "⚠️ Certains tests ont échoué"
    echo "🔧 Vérifiez les erreurs ci-dessus"
    exit 1
fi
