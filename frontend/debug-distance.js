// Script de débogage pour tester le service de distance
// À exécuter dans la console du navigateur sur http://localhost:4201

console.log('🔍 Démarrage du test de débogage pour le service de distance');

// Test 1: Vérifier si l'API Google Maps est chargée
console.log('Test 1: Vérification de l\'API Google Maps');
console.log('Google Maps disponible:', !!window.google);
console.log('Google Maps API chargée:', !!(window.google && window.google.maps));

if (window.google && window.google.maps) {
    console.log('✅ Google Maps API est disponible');
    
    // Test 2: Tester le service de directions directement
    console.log('Test 2: Test direct du service de directions');
    
    const directionsService = new google.maps.DirectionsService();
    const request = {
        origin: new google.maps.LatLng(53.4808, -2.2426), // Manchester
        destination: new google.maps.LatLng(53.3864368, -2.1965351), // Stepping Hill Hospital
        travelMode: google.maps.TravelMode.DRIVING,
        drivingOptions: {
            departureTime: new Date(),
            trafficModel: google.maps.TrafficModel.BEST_GUESS
        }
    };
    
    console.log('Requête de directions:', request);
    
    directionsService.route(request, (result, status) => {
        console.log('Réponse du service de directions:', { status, result });
        
        if (status === google.maps.DirectionsStatus.OK) {
            const leg = result.routes[0].legs[0];
            const distanceText = leg.distance.text;
            const durationText = leg.duration_in_traffic?.text || leg.duration.text;
            
            console.log('✅ Succès!');
            console.log('Distance:', distanceText);
            console.log('Durée:', durationText);
            console.log('Résultat complet:', result);
        } else {
            console.error('❌ Erreur du service de directions:', status);
        }
    });
} else {
    console.error('❌ Google Maps API n\'est pas disponible');
}

// Test 3: Vérifier les variables du composant Angular
console.log('Test 3: Vérification des variables du composant');
console.log('__medheadTestDistance disponible:', typeof window.__medheadTestDistance);
console.log('__medheadTriggerSearch disponible:', typeof window.__medheadTriggerSearch);

// Instructions pour l'utilisateur
console.log(`
📋 Instructions pour tester:

1. Ouvrez http://localhost:4201 dans votre navigateur
2. Ouvrez la console développeur (F12)
3. Exécutez ce script de débogage
4. Si le test direct fonctionne, essayez: window.__medheadTestDistance()
5. Si cela ne fonctionne pas, essayez une recherche complète avec: window.__medheadTriggerSearch('Cardiology', 'Manchester, UK')
`);
