-- Script d'initialisation de la base de données MedHead
-- Ce script crée les tables et insère des données réelles d'hôpitaux du Royaume-Uni

-- Création de la table specialities
CREATE TABLE IF NOT EXISTS specialities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table hospitals
CREATE TABLE IF NOT EXISTS hospitals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    city VARCHAR(255) NOT NULL,
    address TEXT,
    available_beds INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table de liaison entre hôpitaux et spécialités
CREATE TABLE IF NOT EXISTS hospital_specialities (
    hospital_id BIGINT NOT NULL REFERENCES hospitals(id) ON DELETE CASCADE,
    speciality_id BIGINT NOT NULL REFERENCES specialities(id) ON DELETE CASCADE,
    PRIMARY KEY (hospital_id, speciality_id)
);

-- Index pour optimiser les requêtes
CREATE INDEX IF NOT EXISTS idx_hospitals_location ON hospitals (latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_hospitals_city ON hospitals (city);
CREATE INDEX IF NOT EXISTS idx_hospital_specialities_hospital ON hospital_specialities (hospital_id);
CREATE INDEX IF NOT EXISTS idx_hospital_specialities_speciality ON hospital_specialities (speciality_id);

-- Insertion des spécialités médicales
INSERT INTO specialities (name, description) VALUES
('Accident and Emergency', 'Services d''urgence et de soins critiques'),
('Anaesthetics', 'Anesthésie et réanimation'),
('Cardiology', 'Cardiologie et maladies cardiovasculaires'),
('Cardiothoracic Surgery', 'Chirurgie cardiothoracique'),
('Clinical Oncology', 'Oncologie clinique'),
('Clinical Radiology', 'Radiologie clinique et imagerie médicale'),
('Dermatology', 'Dermatologie et maladies de la peau'),
('Emergency Medicine', 'Médecine d''urgence'),
('Endocrinology and Diabetes', 'Endocrinologie et diabétologie'),
('Gastroenterology', 'Gastro-entérologie'),
('General Medicine', 'Médecine générale'),
('General Surgery', 'Chirurgie générale'),
('Geriatric Medicine', 'Médecine gériatrique'),
('Haematology', 'Hématologie'),
('Infectious Diseases', 'Maladies infectieuses'),
('Intensive Care Medicine', 'Médecine intensive et réanimation'),
('Medical Oncology', 'Oncologie médicale'),
('Neurology', 'Neurologie'),
('Neurosurgery', 'Neurochirurgie'),
('Obstetrics and Gynaecology', 'Obstétrique et gynécologie'),
('Ophthalmology', 'Ophtalmologie'),
('Oral and Maxillofacial Surgery', 'Chirurgie orale et maxillo-faciale'),
('Orthopaedic Surgery', 'Chirurgie orthopédique'),
('Otolaryngology', 'Oto-rhino-laryngologie (ORL)'),
('Paediatrics', 'Pédiatrie'),
('Pathology', 'Pathologie et anatomie pathologique'),
('Plastic Surgery', 'Chirurgie plastique et reconstructive'),
('Psychiatry', 'Psychiatrie'),
('Public Health Medicine', 'Santé publique'),
('Respiratory Medicine', 'Médecine respiratoire'),
('Rheumatology', 'Rhumatologie'),
('Trauma and Orthopaedic Surgery', 'Chirurgie traumatologique et orthopédique'),
('Urology', 'Urologie'),
('Vascular Surgery', 'Chirurgie vasculaire');

-- Insertion des hôpitaux du Royaume-Uni
INSERT INTO hospitals (name, latitude, longitude, city, address, available_beds) VALUES
-- Londres
('Guy''s Hospital', 51.5044, -0.0865, 'Londres', 'Great Maze Pond, London SE1 9RT', 25),
('King''s College Hospital', 51.4686, -0.0994, 'Londres', 'Denmark Hill, London SE5 9RS', 30),
('St Thomas'' Hospital', 51.4994, -0.1189, 'Londres', 'Westminster Bridge Rd, London SE1 7EH', 28),
('Royal Free Hospital', 51.5503, -0.1733, 'Londres', 'Pond St, London NW3 2QG', 22),
('University College Hospital', 51.5245, -0.1347, 'Londres', '235 Euston Rd, London NW1 2BU', 26),
('St George''s Hospital', 51.4253, -0.1780, 'Londres', 'Blackshaw Rd, London SW17 0QT', 24),

-- Manchester
('Manchester Royal Infirmary', 53.4592, -2.2264, 'Manchester', 'Oxford Rd, Manchester M13 9WL', 35),
('Salford Royal Hospital', 53.4858, -2.2964, 'Salford', 'Stott Ln, Salford M6 8HD', 28),
('Wythenshawe Hospital', 53.3867, -2.2698, 'Manchester', 'Southmoor Rd, Manchester M23 9LT', 20),

-- Birmingham
('Queen Elizabeth Hospital', 52.4504, -1.9378, 'Birmingham', 'Mindelsohn Way, Birmingham B15 2WB', 32),
('Birmingham Children''s Hospital', 52.4854, -1.8983, 'Birmingham', 'Steelhouse Ln, Birmingham B4 6NH', 15),
('Heartlands Hospital', 52.4709, -1.8229, 'Birmingham', 'Bordesley Green E, Birmingham B9 5SS', 25),

-- Leeds
('Leeds General Infirmary', 53.8013, -1.5486, 'Leeds', 'Great George St, Leeds LS1 3EX', 30),
('St James''s University Hospital', 53.8125, -1.5429, 'Leeds', 'Beckett St, Leeds LS9 7TF', 28),

-- Liverpool
('Royal Liverpool University Hospital', 53.4084, -2.9916, 'Liverpool', 'Prescot St, Liverpool L7 8XP', 26),
('Alder Hey Children''s Hospital', 53.4177, -2.8991, 'Liverpool', 'E Prescot Rd, Liverpool L14 5AB', 18),

-- Newcastle
('Royal Victoria Infirmary', 54.9804, -1.6194, 'Newcastle upon Tyne', 'Queen Victoria Rd, Newcastle upon Tyne NE1 4LP', 29),
('Freeman Hospital', 55.0129, -1.6344, 'Newcastle upon Tyne', 'Freeman Rd, Newcastle upon Tyne NE7 7DN', 24),

-- Bristol
('Bristol Royal Infirmary', 51.4584, -2.6042, 'Bristol', 'Upper Maudlin St, Bristol BS2 8HW', 27),
('Southmead Hospital', 51.4934, -2.5874, 'Bristol', 'Southmead Rd, Bristol BS10 5NB', 31),

-- Sheffield
('Royal Hallamshire Hospital', 53.3811, -1.4701, 'Sheffield', 'Glossop Rd, Sheffield S10 2JF', 23),
('Northern General Hospital', 53.4129, -1.4334, 'Sheffield', 'Herries Rd, Sheffield S5 7AU', 26),

-- Nottingham
('Queen''s Medical Centre', 52.9452, -1.1864, 'Nottingham', 'Derby Rd, Nottingham NG7 2UH', 33),
('City Hospital', 52.9551, -1.1516, 'Nottingham', 'Hucknall Rd, Nottingham NG5 1PB', 21),

-- Leicester
('Leicester Royal Infirmary', 52.6320, -1.1358, 'Leicester', 'Infirmary Sq, Leicester LE1 5WW', 28),
('Glenfield Hospital', 52.6444, -1.1425, 'Leicester', 'Groby Rd, Leicester LE3 9QP', 22),

-- Cardiff
('University Hospital of Wales', 51.5074, -3.1908, 'Cardiff', 'Heath Park, Cardiff CF14 4XW', 34),
('Cardiff Royal Infirmary', 51.4905, -3.1701, 'Cardiff', 'Newport Rd, Cardiff CF24 0SZ', 19),

-- Edinburgh
('Royal Infirmary of Edinburgh', 55.9244, -3.1331, 'Edinburgh', '51 Little France Cres, Edinburgh EH16 4SA', 32),
('Western General Hospital', 55.9624, -3.2331, 'Edinburgh', 'Crewe Rd S, Edinburgh EH4 2XU', 25),

-- Glasgow
('Queen Elizabeth University Hospital', 55.8698, -4.3318, 'Glasgow', '1345 Govan Rd, Glasgow G51 4TF', 38),
('Glasgow Royal Infirmary', 55.8642, -4.2518, 'Glasgow', '84 Castle St, Glasgow G4 0SF', 29),

-- Belfast
('Royal Victoria Hospital', 54.5933, -5.9334, 'Belfast', 'Grosvenor Rd, Belfast BT12 6BA', 31),
('Belfast City Hospital', 54.5831, -5.9167, 'Belfast', 'Lisburn Rd, Belfast BT9 7AB', 24);

-- Association des hôpitaux avec leurs spécialités
-- Guy's Hospital - Spécialités principales
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(1, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(1, (SELECT id FROM specialities WHERE name = 'Neurology')),
(1, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(1, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(1, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- King's College Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(2, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(2, (SELECT id FROM specialities WHERE name = 'Neurology')),
(2, (SELECT id FROM specialities WHERE name = 'Neurosurgery')),
(2, (SELECT id FROM specialities WHERE name = 'Paediatrics')),
(2, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- St Thomas' Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(3, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(3, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(3, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(3, (SELECT id FROM specialities WHERE name = 'Obstetrics and Gynaecology')),
(3, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Royal Free Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(4, (SELECT id FROM specialities WHERE name = 'Haematology')),
(4, (SELECT id FROM specialities WHERE name = 'Infectious Diseases')),
(4, (SELECT id FROM specialities WHERE name = 'Gastroenterology')),
(4, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(4, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- University College Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(5, (SELECT id FROM specialities WHERE name = 'Clinical Oncology')),
(5, (SELECT id FROM specialities WHERE name = 'Neurology')),
(5, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(5, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(5, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- St George's Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(6, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(6, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(6, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(6, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Manchester Royal Infirmary
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(7, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(7, (SELECT id FROM specialities WHERE name = 'Neurology')),
(7, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(7, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(7, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Salford Royal Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(8, (SELECT id FROM specialities WHERE name = 'Neurology')),
(8, (SELECT id FROM specialities WHERE name = 'Neurosurgery')),
(8, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(8, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Wythenshawe Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(9, (SELECT id FROM specialities WHERE name = 'Cardiothoracic Surgery')),
(9, (SELECT id FROM specialities WHERE name = 'Respiratory Medicine')),
(9, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(9, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Queen Elizabeth Hospital Birmingham
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(10, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(10, (SELECT id FROM specialities WHERE name = 'Neurology')),
(10, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(10, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(10, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Birmingham Children's Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(11, (SELECT id FROM specialities WHERE name = 'Paediatrics')),
(11, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(11, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Heartlands Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(12, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(12, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(12, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(12, (SELECT id FROM specialities WHERE name = 'Orthopaedic Surgery'));

-- Leeds General Infirmary
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(13, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(13, (SELECT id FROM specialities WHERE name = 'Neurology')),
(13, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(13, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(13, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- St James's University Hospital Leeds
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(14, (SELECT id FROM specialities WHERE name = 'Medical Oncology')),
(14, (SELECT id FROM specialities WHERE name = 'Haematology')),
(14, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(14, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Royal Liverpool University Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(15, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(15, (SELECT id FROM specialities WHERE name = 'Neurology')),
(15, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(15, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(15, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Alder Hey Children's Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(16, (SELECT id FROM specialities WHERE name = 'Paediatrics')),
(16, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(16, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Royal Victoria Infirmary Newcastle
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(17, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(17, (SELECT id FROM specialities WHERE name = 'Neurology')),
(17, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(17, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(17, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Freeman Hospital Newcastle
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(18, (SELECT id FROM specialities WHERE name = 'Cardiothoracic Surgery')),
(18, (SELECT id FROM specialities WHERE name = 'Respiratory Medicine')),
(18, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(18, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Bristol Royal Infirmary
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(19, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(19, (SELECT id FROM specialities WHERE name = 'Neurology')),
(19, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(19, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(19, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Southmead Hospital Bristol
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(20, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(20, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(20, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(20, (SELECT id FROM specialities WHERE name = 'Orthopaedic Surgery'));

-- Royal Hallamshire Hospital Sheffield
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(21, (SELECT id FROM specialities WHERE name = 'Neurology')),
(21, (SELECT id FROM specialities WHERE name = 'Neurosurgery')),
(21, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(21, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Northern General Hospital Sheffield
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(22, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(22, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(22, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(22, (SELECT id FROM specialities WHERE name = 'Orthopaedic Surgery'));

-- Queen's Medical Centre Nottingham
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(23, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(23, (SELECT id FROM specialities WHERE name = 'Neurology')),
(23, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(23, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(23, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- City Hospital Nottingham
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(24, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(24, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(24, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(24, (SELECT id FROM specialities WHERE name = 'Orthopaedic Surgery'));

-- Leicester Royal Infirmary
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(25, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(25, (SELECT id FROM specialities WHERE name = 'Neurology')),
(25, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(25, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(25, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Glenfield Hospital Leicester
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(26, (SELECT id FROM specialities WHERE name = 'Cardiothoracic Surgery')),
(26, (SELECT id FROM specialities WHERE name = 'Respiratory Medicine')),
(26, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(26, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- University Hospital of Wales Cardiff
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(27, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(27, (SELECT id FROM specialities WHERE name = 'Neurology')),
(27, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(27, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(27, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Cardiff Royal Infirmary
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(28, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(28, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(28, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(28, (SELECT id FROM specialities WHERE name = 'Orthopaedic Surgery'));

-- Royal Infirmary of Edinburgh
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(29, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(29, (SELECT id FROM specialities WHERE name = 'Neurology')),
(29, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(29, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(29, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Western General Hospital Edinburgh
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(30, (SELECT id FROM specialities WHERE name = 'Medical Oncology')),
(30, (SELECT id FROM specialities WHERE name = 'Haematology')),
(30, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(30, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Queen Elizabeth University Hospital Glasgow
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(31, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(31, (SELECT id FROM specialities WHERE name = 'Neurology')),
(31, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(31, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(31, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Glasgow Royal Infirmary
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(32, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(32, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(32, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(32, (SELECT id FROM specialities WHERE name = 'Orthopaedic Surgery'));

-- Royal Victoria Hospital Belfast
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(33, (SELECT id FROM specialities WHERE name = 'Cardiology')),
(33, (SELECT id FROM specialities WHERE name = 'Neurology')),
(33, (SELECT id FROM specialities WHERE name = 'General Surgery')),
(33, (SELECT id FROM specialities WHERE name = 'Emergency Medicine')),
(33, (SELECT id FROM specialities WHERE name = 'Intensive Care Medicine'));

-- Belfast City Hospital
INSERT INTO hospital_specialities (hospital_id, speciality_id) VALUES
(34, (SELECT id FROM specialities WHERE name = 'Medical Oncology')),
(34, (SELECT id FROM specialities WHERE name = 'Haematology')),
(34, (SELECT id FROM specialities WHERE name = 'General Medicine')),
(34, (SELECT id FROM specialities WHERE name = 'Emergency Medicine'));

-- Création d'une fonction pour mettre à jour automatiquement updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Création du trigger pour updated_at
DROP TRIGGER IF EXISTS update_hospitals_updated_at ON hospitals;
CREATE TRIGGER update_hospitals_updated_at
    BEFORE UPDATE ON hospitals
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Affichage des statistiques
SELECT COUNT(*) as total_hospitals FROM hospitals;
SELECT COUNT(*) as total_specialities FROM specialities;
SELECT COUNT(*) as total_hospital_specialities FROM hospital_specialities;
SELECT 'Données d''hôpitaux du Royaume-Uni initialisées avec succès!' as message;
