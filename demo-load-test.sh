#!/bin/bash

# Script de démonstration des tests de charge K6 pour MedHead
# Ce script guide l'utilisateur à travers le processus complet

set -e

echo "🏥 MedHead Hospital Allocation - Démonstration des Tests de Charge"
echo "=================================================================="
echo ""

# Vérification de K6
if ! command -v k6 &> /dev/null; then
    echo "❌ K6 n'est pas installé."
    echo "💡 Installation automatique..."
    ./install-k6.sh
    echo ""
fi

echo "✅ K6 est installé: $(k6 version)"
echo ""

# Vérification de l'application
echo "🔍 Vérification de l'application MedHead..."
if curl -s http://localhost:4200/api/health > /dev/null; then
    echo "✅ Application MedHead est en cours d'exécution"
else
    echo "❌ Application MedHead n'est pas accessible sur http://localhost:4200"
    echo "💡 Démarrez l'application avec:"
    echo "   cd docker && ./start-medhead.sh"
    echo ""
    read -p "Voulez-vous continuer quand même? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "🎯 Objectifs du test de charge:"
echo "   • Charge cible: 800 requêtes/seconde"
echo "   • Temps de réponse: < 200ms (95% des requêtes)"
echo "   • Taux d'erreur: < 1%"
echo "   • Endpoint: POST /api/allocate"
echo ""

# Menu interactif
while true; do
    echo "📋 Choisissez une option:"
    echo "1) 🚀 Test rapide de connectivité (10 secondes)"
    echo "2) 🔥 Test de charge complet (800 req/s, 6 minutes)"
    echo "3) 📊 Test de charge personnalisé"
    echo "4) 📚 Afficher la documentation"
    echo "5) ❌ Quitter"
    echo ""
    read -p "Votre choix (1-5): " choice

    case $choice in
        1)
            echo ""
            echo "🚀 Lancement du test rapide..."
            echo "================================"
            k6 run quick-test.js
            echo ""
            ;;
        2)
            echo ""
            echo "🔥 Lancement du test de charge complet..."
            echo "=========================================="
            echo "⚠️  Ce test va générer une charge de 800 req/s pendant 6 minutes"
            echo "📊 Surveillez les métriques de performance"
            echo ""
            read -p "Continuer? (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                k6 run test.js
            fi
            echo ""
            ;;
        3)
            echo ""
            echo "📊 Configuration du test personnalisé..."
            echo "========================================"
            read -p "Nombre de requêtes/seconde (défaut: 400): " rps
            read -p "Durée en minutes (défaut: 2): " duration
            read -p "Seuil de temps de réponse en ms (défaut: 200): " threshold
            
            rps=${rps:-400}
            duration=${duration:-2}
            threshold=${threshold:-200}
            
            echo ""
            echo "🔧 Génération du test personnalisé..."
            cat > custom-test.js << EOF
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: ${rps} },
    { duration: '${duration}m', target: ${rps} },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<${threshold}'],
    http_req_failed: ['rate<0.01'],
  },
};

const testData = {
  specialty: 'Cardiology',
  latitude: 53.3976314,
  longitude: -2.1829641
};

export default function () {
  const payload = JSON.stringify(testData);
  const response = http.post('http://localhost:4200/api/allocate', payload, {
    headers: { 'Content-Type': 'application/json' },
  });
  
  check(response, {
    'Status is 200': (r) => r.status === 200,
    'Response time < ${threshold}ms': (r) => r.timings.duration < ${threshold},
  });
  
  sleep(0.1);
}
EOF
            
            echo "✅ Test personnalisé généré: custom-test.js"
            echo "🚀 Lancement du test..."
            k6 run custom-test.js
            echo ""
            ;;
        4)
            echo ""
            echo "📚 Documentation des tests de charge"
            echo "===================================="
            if [ -f "LOAD_TEST_GUIDE.md" ]; then
                cat LOAD_TEST_GUIDE.md
            else
                echo "❌ Fichier de documentation non trouvé"
            fi
            echo ""
            ;;
        5)
            echo ""
            echo "👋 Au revoir!"
            exit 0
            ;;
        *)
            echo "❌ Choix invalide. Veuillez choisir entre 1 et 5."
            echo ""
            ;;
    esac
done
