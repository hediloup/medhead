#!/bin/bash

# Script d'installation de K6 pour MedHead Load Testing
# Supporte Ubuntu/Debian, CentOS/RHEL, et macOS

set -e

echo "🚀 Installation de K6 pour les tests de charge MedHead..."

# Détection du système d'exploitation
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    if command -v apt-get &> /dev/null; then
        # Ubuntu/Debian
        echo "📦 Installation sur Ubuntu/Debian..."
        sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
        echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
        sudo apt-get update
        sudo apt-get install k6 -y
    elif command -v yum &> /dev/null; then
        # CentOS/RHEL
        echo "📦 Installation sur CentOS/RHEL..."
        sudo yum install https://dl.k6.io/rpm/repo.rpm -y
        sudo yum install k6 -y
    else
        echo "❌ Distribution Linux non supportée. Veuillez installer K6 manuellement."
        exit 1
    fi
elif [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    if command -v brew &> /dev/null; then
        echo "📦 Installation sur macOS avec Homebrew..."
        brew install k6
    else
        echo "❌ Homebrew non trouvé. Veuillez installer Homebrew ou K6 manuellement."
        exit 1
    fi
else
    echo "❌ Système d'exploitation non supporté: $OSTYPE"
    echo "Veuillez consulter https://k6.io/docs/getting-started/installation/ pour l'installation manuelle."
    exit 1
fi

# Vérification de l'installation
if command -v k6 &> /dev/null; then
    echo "✅ K6 installé avec succès!"
    echo "Version: $(k6 version)"
    echo ""
    echo "🎯 Prochaines étapes:"
    echo "1. Démarrer l'application MedHead:"
    echo "   cd docker && ./start-medhead.sh"
    echo ""
    echo "2. Tester la connectivité:"
    echo "   k6 run quick-test.js"
    echo ""
    echo "3. Lancer le test de charge complet:"
    echo "   k6 run test.js"
    echo ""
    echo "📚 Documentation: LOAD_TEST_GUIDE.md"
else
    echo "❌ Échec de l'installation de K6"
    exit 1
fi
