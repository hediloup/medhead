#!/bin/bash

# Script pour arrêter l'application MedHead
# Usage: ./stop-app.sh [port]

set -e

APP_PORT=${1:-8080}
APP_NAME="medhead"

echo "🛑 Arrêt de l'application MedHead..."
echo "=================================="

# Méthode 1: Par port
echo "🔍 Recherche sur le port $APP_PORT..."
PORT_PID=$(lsof -ti:$APP_PORT 2>/dev/null || true)

if [ ! -z "$PORT_PID" ]; then
    echo "📡 Processus trouvé sur le port $APP_PORT (PID: $PORT_PID)"
    echo "🛑 Arrêt du processus..."
    kill $PORT_PID
    sleep 2
    
    # Vérifier si encore en vie
    if kill -0 $PORT_PID 2>/dev/null; then
        echo "⚠️  Processus encore actif, arrêt forcé..."
        kill -9 $PORT_PID
    fi
    echo "✅ Application arrêtée sur le port $APP_PORT"
else
    echo "ℹ️  Aucun processus sur le port $APP_PORT"
fi

# Méthode 2: Par nom d'application
echo ""
echo "🔍 Recherche par nom d'application..."
APP_PIDS=$(pgrep -f "$APP_NAME" 2>/dev/null || true)

if [ ! -z "$APP_PIDS" ]; then
    echo "📱 Processus MedHead trouvés: $APP_PIDS"
    echo "🛑 Arrêt des processus..."
    pkill -f "$APP_NAME"
    sleep 2
    
    # Vérifier les processus restants
    REMAINING=$(pgrep -f "$APP_NAME" 2>/dev/null || true)
    if [ ! -z "$REMAINING" ]; then
        echo "⚠️  Arrêt forcé des processus restants..."
        pkill -9 -f "$APP_NAME"
    fi
    echo "✅ Tous les processus MedHead arrêtés"
else
    echo "ℹ️  Aucun processus MedHead trouvé"
fi

# Vérification finale
echo ""
echo "🔍 Vérification finale..."
REMAINING_PROCESSES=$(ps aux | grep java | grep -i medhead | grep -v grep || true)

if [ -z "$REMAINING_PROCESSES" ]; then
    echo "✅ Application MedHead complètement arrêtée"
else
    echo "⚠️  Certains processus persistent:"
    echo "$REMAINING_PROCESSES"
fi

echo ""
echo "🎉 Script d'arrêt terminé"
