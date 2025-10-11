-- Script pour créer la table patients avec protection RGPD
-- Ce script s'exécute après l'initialisation de la base de données

-- Création de la table patients
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    patient_uuid VARCHAR(255) NOT NULL UNIQUE,
    anonymized_name VARCHAR(255),
    age_group VARCHAR(10),
    gender VARCHAR(1),
    postal_code VARCHAR(10),
    required_specialty VARCHAR(255) NOT NULL,
    severity_level VARCHAR(20),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_retention_until TIMESTAMP NOT NULL,
    is_anonymized BOOLEAN NOT NULL DEFAULT FALSE,
    allocated_hospital_id BIGINT REFERENCES hospitals(id)
);

-- Index pour optimiser les requêtes
CREATE INDEX IF NOT EXISTS idx_patients_uuid ON patients (patient_uuid);
CREATE INDEX IF NOT EXISTS idx_patients_anonymized ON patients (is_anonymized);
CREATE INDEX IF NOT EXISTS idx_patients_retention ON patients (data_retention_until);
CREATE INDEX IF NOT EXISTS idx_patients_specialty ON patients (required_specialty);
CREATE INDEX IF NOT EXISTS idx_patients_severity ON patients (severity_level);
CREATE INDEX IF NOT EXISTS idx_patients_age_group ON patients (age_group);
CREATE INDEX IF NOT EXISTS idx_patients_hospital ON patients (allocated_hospital_id);
CREATE INDEX IF NOT EXISTS idx_patients_location ON patients (latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_patients_created_at ON patients (created_at);

-- Création d'une fonction pour mettre à jour automatiquement updated_at pour patients
CREATE OR REPLACE FUNCTION update_patients_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Création du trigger pour updated_at sur patients
DROP TRIGGER IF EXISTS update_patients_updated_at ON patients;
CREATE TRIGGER update_patients_updated_at
    BEFORE UPDATE ON patients
    FOR EACH ROW
    EXECUTE FUNCTION update_patients_updated_at_column();

-- Création d'une fonction pour nettoyer automatiquement les données expirées
CREATE OR REPLACE FUNCTION cleanup_expired_patients()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM patients WHERE data_retention_until < CURRENT_TIMESTAMP;
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ language 'plpgsql';

-- Création d'une vue pour les statistiques anonymisées
CREATE OR REPLACE VIEW patient_statistics AS
SELECT 
    required_specialty,
    severity_level,
    age_group,
    COUNT(*) as patient_count,
    COUNT(CASE WHEN is_anonymized THEN 1 END) as anonymized_count,
    COUNT(CASE WHEN allocated_hospital_id IS NOT NULL THEN 1 END) as allocated_count,
    DATE_TRUNC('day', created_at) as allocation_date
FROM patients 
WHERE data_retention_until > CURRENT_TIMESTAMP
GROUP BY required_specialty, severity_level, age_group, DATE_TRUNC('day', created_at);

-- Création d'une vue pour les analyses géographiques anonymisées
CREATE OR REPLACE VIEW geographic_patient_analysis AS
SELECT 
    postal_code,
    required_specialty,
    COUNT(*) as patient_count,
    AVG(latitude) as avg_latitude,
    AVG(longitude) as avg_longitude,
    COUNT(CASE WHEN allocated_hospital_id IS NOT NULL THEN 1 END) as allocated_count
FROM patients 
WHERE postal_code IS NOT NULL 
  AND data_retention_until > CURRENT_TIMESTAMP
GROUP BY postal_code, required_specialty;

-- Insertion de quelques patients de test (anonymisés)
INSERT INTO patients (
    patient_uuid, anonymized_name, age_group, gender, postal_code,
    required_specialty, severity_level, latitude, longitude,
    data_retention_until, is_anonymized, allocated_hospital_id
) VALUES
-- Patient 1 - Cardiologie à Londres
('550e8400-e29b-41d4-a716-446655440001', 'PATIENT_550E8400', '19-65', 'M', 'SW1A 1AA',
 'Cardiology', 'HIGH', 51.5074, -0.1278,
 CURRENT_TIMESTAMP + INTERVAL '7 years', TRUE, 1),

-- Patient 2 - Neurologie à Manchester  
('550e8400-e29b-41d4-a716-446655440002', 'PATIENT_550E8401', '65+', 'F', 'M1 1AA',
 'Neurology', 'MEDIUM', 53.4592, -2.2264,
 CURRENT_TIMESTAMP + INTERVAL '7 years', TRUE, 7),

-- Patient 3 - Urgences à Birmingham
('550e8400-e29b-41d4-a716-446655440003', 'PATIENT_550E8402', '0-18', 'M', 'B1 1AA',
 'Emergency Medicine', 'CRITICAL', 52.4504, -1.9378,
 CURRENT_TIMESTAMP + INTERVAL '7 years', TRUE, 10),

-- Patient 4 - Chirurgie à Leeds
('550e8400-e29b-41d4-a716-446655440004', 'PATIENT_550E8403', '19-65', 'F', 'LS1 1AA',
 'General Surgery', 'MEDIUM', 53.8013, -1.5486,
 CURRENT_TIMESTAMP + INTERVAL '7 years', TRUE, 13);

-- Affichage des statistiques
SELECT 'Table patients créée avec succès!' as message;
SELECT COUNT(*) as total_patients FROM patients;
SELECT COUNT(*) as anonymized_patients FROM patients WHERE is_anonymized = TRUE;
SELECT COUNT(*) as allocated_patients FROM patients WHERE allocated_hospital_id IS NOT NULL;
