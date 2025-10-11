#!/bin/bash

# Test de l'API Google Maps pour démontrer l'optimisation par trafic
echo "🚀 Test de l'API Google Maps pour l'optimisation par trafic"
echo "============================================================"

# Configuration
GOOGLE_MAPS_API_KEY="AIzaSyA6EicRUZ_a2YTAuB1ZFWWXB6yOaqnKyB0"
ORIGIN="48.8566,2.3522"  # Paris, France
DESTINATION="48.8416,2.2681"  # Paris, France (différent quartier)

echo "📍 Origine: Paris (48.8566, 2.3522)"
echo "🏥 Destination: Paris (48.8416, 2.2681)"
echo ""

# Test 1: Calcul de route avec trafic
echo "🔍 Test 1: Calcul de route avec trafic en temps réel"
echo "---------------------------------------------------"

RESPONSE=$(curl -s "https://maps.googleapis.com/maps/api/directions/json?origin=${ORIGIN}&destination=${DESTINATION}&mode=driving&departure_time=now&traffic_model=best_guess&key=${GOOGLE_MAPS_API_KEY}")

STATUS=$(echo $RESPONSE | grep -o '"status" : "[^"]*"' | cut -d'"' -f4)
echo "Status API: $STATUS"

if [ "$STATUS" = "OK" ]; then
    DISTANCE=$(echo $RESPONSE | grep -o '"distance" : {"text" : "[^"]*"' | cut -d'"' -f6)
    DURATION=$(echo $RESPONSE | grep -o '"duration" : {"text" : "[^"]*"' | cut -d'"' -f6)
    DURATION_TRAFFIC=$(echo $RESPONSE | grep -o '"duration_in_traffic" : {"text" : "[^"]*"' | cut -d'"' -f6)
    
    echo "✅ Route calculée avec succès:"
    echo "   📏 Distance routière: $DISTANCE"
    echo "   ⏱️  Temps sans trafic: $DURATION"
    echo "   🚗 Temps avec trafic: $DURATION_TRAFFIC"
else
    echo "❌ Erreur API: $STATUS"
    echo $RESPONSE | grep -o '"error_message" : "[^"]*"' | cut -d'"' -f4
fi

echo ""
echo "📊 Comparaison avec calcul Haversine (à vol d'oiseau):"
echo "------------------------------------------------------"

# Calcul Haversine simple (approximatif)
LAT1=48.8566
LON1=2.3522
LAT2=48.8416
LON2=2.2681

# Conversion en radians
LAT1_RAD=$(echo "$LAT1 * 3.14159 / 180" | bc -l)
LON1_RAD=$(echo "$LON1 * 3.14159 / 180" | bc -l)
LAT2_RAD=$(echo "$LAT2 * 3.14159 / 180" | bc -l)
LON2_RAD=$(echo "$LON2 * 3.14159 / 180" | bc -l)

# Différences
DLAT=$(echo "$LAT2_RAD - $LAT1_RAD" | bc -l)
DLON=$(echo "$LON2_RAD - $LON1_RAD" | bc -l)

# Formule Haversine
A=$(echo "s($DLAT/2)^2 + c($LAT1_RAD) * c($LAT2_RAD) * s($DLON/2)^2" | bc -l)
C=$(echo "2 * a(sqrt($A) / sqrt(1-$A))" | bc -l)
DISTANCE_KM=$(echo "$C * 6371" | bc -l)

# Temps estimé basé sur vitesse moyenne
TIME_MINUTES=$(echo "$DISTANCE_KM * 60 / 50" | bc -l)

echo "✅ Calcul Haversine (à vol d'oiseau):"
echo "   📏 Distance: $(printf "%.2f" $DISTANCE_KM) km"
echo "   ⏱️  Temps estimé: $(printf "%.0f" $TIME_MINUTES) minutes"

echo ""
echo "🎯 Avantages de l'optimisation par trafic:"
echo "=========================================="
echo "• Distances routières réelles (pas à vol d'oiseau)"
echo "• Prise en compte du trafic en temps réel"
echo "• Optimisation par temps de trajet (plus pertinent en urgence)"
echo "• Considération des limitations de vitesse et intersections"
echo "• Données de trafic mises à jour en continu"
echo ""
echo "🏥 Impact sur l'allocation d'hôpitaux:"
echo "====================================="
echo "• Sélection de l'hôpital avec le temps d'arrivée le plus court"
echo "• Meilleure efficacité pour les urgences médicales"
echo "• Réduction des temps de transport des patients"
echo "• Optimisation des ressources d'ambulances"
