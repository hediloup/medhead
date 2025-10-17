#!/bin/bash

# Startup script for the complete MedHead application with Docker
echo "🚀 Starting the complete MedHead application with Docker..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker Desktop."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose."
    exit 1
fi

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Clean up existing images (optional)
echo "🧹 Cleaning up existing images..."
docker system prune -f

# Build and start all services
echo "🔨 Building and starting all services..."
echo "   📊 PostgreSQL (database)"
echo "   🔧 Backend Spring Boot"
echo "   🎨 Frontend Angular"
echo "   🛠️  pgAdmin (optional)"

docker-compose up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 45

# Check services status
echo "📊 Services status:"
docker-compose ps

# Display logs
echo "📝 Services logs:"
docker-compose logs --tail=20

echo ""
echo "✅ Complete MedHead application started successfully!"
echo ""
echo "🌐 Service access:"
echo "   🎨 Frontend Angular: http://localhost:4200"
echo "   🔧 Backend API:      http://localhost:8080"
echo "   📊 PostgreSQL DB:    localhost:5433"
echo "   🛠️  pgAdmin:         http://localhost:8082"
echo ""
echo "📋 Connection information:"
echo "   Database:"
echo "     Host: localhost"
echo "     Port: 5433"
echo "     Database: medhead_db"
echo "     Username: medhead_user"
echo "     Password: (not required - trust authentication)"
echo "     Note: PostgreSQL is configured with trust authentication"
echo ""
echo "   pgAdmin:"
echo "     Email: admin@medhead.com"
echo "     Password: admin123"
echo ""
echo "📋 Useful commands:"
echo "   View logs:       docker-compose logs -f"
echo "   Stop:            docker-compose down"
echo "   Restart:         docker-compose restart"
echo "   Clean up:        docker-compose down -v"
echo ""
echo "🧪 Quick tests:"
echo "   API Health:      curl http://localhost:8080/api/health"
echo "   Frontend:        curl http://localhost:4200"
echo ""
