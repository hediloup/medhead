-- Script de correction des coordonnées du Stepping Hill Hospital
-- À exécuter directement sur la base de données existante

-- Vérifier les coordonnées actuelles
SELECT id, name, latitude, longitude, address 
FROM hospitals 
WHERE name = 'Stepping Hill Hospital';

-- Mettre à jour les coordonnées avec les bonnes valeurs
UPDATE hospitals 
SET 
    latitude = 53.38451,
    longitude = -2.13321,
    updated_at = CURRENT_TIMESTAMP
WHERE name = 'Stepping Hill Hospital';

-- Vérifier la correction
SELECT id, name, latitude, longitude, address 
FROM hospitals 
WHERE name = 'Stepping Hill Hospital';

-- Message de confirmation
SELECT 'Coordonnées du Stepping Hill Hospital corrigées avec succès!' as message;
