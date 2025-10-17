-- Script de données de test pour MedHead
-- Ce script ajoute des données de test supplémentaires si nécessaire

-- Vérifier si on est en mode test (variable d'environnement)
-- Si TEST_MODE=true, ajouter des données de test supplémentaires

-- Ajouter des données de test seulement si elles n'existent pas déjà
INSERT INTO specialities (name, description, created_at) 
SELECT 'Test Specialty', 'Specialty for testing purposes', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM specialities WHERE name = 'Test Specialty');

-- Ajouter un hôpital de test seulement s'il n'existe pas
INSERT INTO hospitals (name, latitude, longitude, city, address, available_beds, created_at) 
SELECT 'Test Hospital', 53.5, -2.2, 'Test City', 'Test Address', 100, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM hospitals WHERE name = 'Test Hospital');

-- Associer la spécialité de test à l'hôpital de test
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE h.name = 'Test Hospital' 
AND s.name = 'Test Specialty'
AND NOT EXISTS (
    SELECT 1 FROM hospital_specialities hs 
    WHERE hs.hospital_id = h.id AND hs.speciality_id = s.id
);
