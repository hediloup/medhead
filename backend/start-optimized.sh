#!/bin/bash

# Script de démarrage optimisé pour MedHead Backend
# Applique les optimisations JVM recommandées pour de meilleures performances

echo "🚀 Starting MedHead Backend with Performance Optimizations..."

# Configuration JVM optimisée
export JAVA_OPTS="-Xms512m -Xmx2g \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-XX:+UseStringDeduplication \
-XX:+OptimizeStringConcat \
-XX:+UseCompressedOops \
-XX:+UseCompressedClassPointers \
-XX:+TieredCompilation \
-XX:TieredStopAtLevel=1 \
-Dspring.profiles.active=prod \
-Djava.security.egd=file:/dev/./urandom"

# Variables d'environnement pour les performances
export SPRING_PROFILES_ACTIVE=prod
export SERVER_TOMCAT_THREADS_MAX=200
export SERVER_TOMCAT_THREADS_MIN_SPARE=20

echo "📊 JVM Configuration:"
echo "  - Heap: 512MB - 2GB"
echo "  - GC: G1GC with 200ms max pause"
echo "  - Compilation: Tiered"
echo "  - Profile: production"

# Démarrer l'application
java $JAVA_OPTS -jar target/poc-0.0.1-SNAPSHOT.jar

echo "✅ MedHead Backend started with optimizations"
