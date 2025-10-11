#!/bin/bash

# Script de démarrage pour l'application MedHead complète avec Docker
echo "🚀 Démarrage de l'application MedHead complète avec Docker..."

# Vérifier si Docker est installé
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé. Veuillez installer Docker Desktop."
    exit 1
fi

# Vérifier si Docker Compose est installé
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose n'est pas installé. Veuillez installer Docker Compose."
    exit 1
fi

# Arrêter les conteneurs existants
echo "🛑 Arrêt des conteneurs existants..."
docker-compose down

# Nettoyer les images existantes (optionnel)
echo "🧹 Nettoyage des images existantes..."
docker system prune -f

# Construire et démarrer tous les services
echo "🔨 Construction et démarrage de tous les services..."
echo "   📊 PostgreSQL (base de données)"
echo "   🔧 Backend Spring Boot"
echo "   🎨 Frontend Angular"
echo "   🛠️  pgAdmin (optionnel)"

docker-compose up --build -d

# Attendre que les services soient prêts
echo "⏳ Attente du démarrage des services..."
sleep 45

# Vérifier le statut des services
echo "📊 Statut des services:"
docker-compose ps

# Afficher les logs
echo "📝 Logs des services:"
docker-compose logs --tail=20

echo ""
echo "✅ Application MedHead complète démarrée avec succès!"
echo ""
echo "🌐 Accès aux services:"
echo "   🎨 Frontend Angular: http://localhost:4200"
echo "   🔧 Backend API:      http://localhost:8080"
echo "   📊 Base PostgreSQL:  localhost:5433"
echo "   🛠️  pgAdmin:         http://localhost:8081"
echo ""
echo "📋 Informations de connexion:"
echo "   Base de données:"
echo "     Host: localhost"
echo "     Port: 5433"
echo "     Database: medhead_db"
echo "     Username: medhead_user"
echo "     Password: medhead_password"
echo ""
echo "   pgAdmin:"
echo "     Email: admin@medhead.com"
echo "     Password: admin123"
echo ""
echo "📋 Commandes utiles:"
echo "   Voir les logs:     docker-compose logs -f"
echo "   Arrêter:           docker-compose down"
echo "   Redémarrer:        docker-compose restart"
echo "   Nettoyer:          docker-compose down -v"
echo ""
echo "🧪 Tests rapides:"
echo "   API Health:        curl http://localhost:8080/api/health"
echo "   Frontend:          curl http://localhost:4200"
echo ""
