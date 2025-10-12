-- Script d'importation des hôpitaux du Royaume-Uni avec leurs spécialités
-- Ce script initialise la base de données avec des hôpitaux réels du Royaume-Uni

-- Insertion des spécialités médicales
INSERT INTO specialities (name, description, created_at) VALUES 
('Cardiology', 'Spécialité médicale traitant les maladies du cœur et du système cardiovasculaire', CURRENT_TIMESTAMP),
('Emergency Medicine', 'Médecine d''urgence pour les situations critiques nécessitant une intervention immédiate', CURRENT_TIMESTAMP),
('Neurology', 'Spécialité médicale traitant les maladies du système nerveux', CURRENT_TIMESTAMP),
('Oncology', 'Spécialité médicale traitant les cancers et tumeurs', CURRENT_TIMESTAMP),
('Orthopedics', 'Spécialité médicale traitant les maladies des os, articulations et muscles', CURRENT_TIMESTAMP),
('Pediatrics', 'Spécialité médicale traitant les maladies des enfants', CURRENT_TIMESTAMP),
('Gynecology', 'Spécialité médicale traitant les maladies de l''appareil génital féminin', CURRENT_TIMESTAMP),
('Urology', 'Spécialité médicale traitant les maladies de l''appareil urinaire', CURRENT_TIMESTAMP),
('Ophthalmology', 'Spécialité médicale traitant les maladies des yeux', CURRENT_TIMESTAMP),
('Dermatology', 'Spécialité médicale traitant les maladies de la peau', CURRENT_TIMESTAMP),
('Psychiatry', 'Spécialité médicale traitant les maladies mentales', CURRENT_TIMESTAMP),
('Anesthesiology', 'Spécialité médicale pour l''anesthésie et la réanimation', CURRENT_TIMESTAMP),
('Radiology', 'Spécialité médicale utilisant l''imagerie médicale pour le diagnostic', CURRENT_TIMESTAMP),
('Immunology', 'Spécialité médicale traitant les maladies du système immunitaire', CURRENT_TIMESTAMP),
('Endocrinology', 'Spécialité médicale traitant les maladies des glandes endocrines', CURRENT_TIMESTAMP);

-- Insertion des hôpitaux du Royaume-Uni avec leurs coordonnées
-- Hôpitaux près de Stockport et Manchester
INSERT INTO hospitals (name, latitude, longitude, city, address, available_beds, created_at) VALUES 
-- Hôpitaux près de Stockport (SK3 0TN)
('Stepping Hill Hospital', 53.3969, -2.1333, 'Stockport', 'Poplar Grove, Stockport SK2 7JE', 450, CURRENT_TIMESTAMP),
('Manchester Royal Infirmary', 53.4598, -2.2270, 'Manchester', 'Oxford Rd, Manchester M13 9WL', 800, CURRENT_TIMESTAMP),
('Wythenshawe Hospital', 53.3956, -2.2700, 'Manchester', 'Southmoor Rd, Wythenshawe, Manchester M23 9LT', 600, CURRENT_TIMESTAMP),
('North Manchester General Hospital', 53.5264, -2.2144, 'Manchester', 'Delaunays Rd, Crumpsall, Manchester M8 5RB', 400, CURRENT_TIMESTAMP),

-- Hôpitaux de Londres
('St Thomas Hospital', 51.4991, -0.1189, 'London', 'Westminster Bridge Rd, London SE1 7EH', 850, CURRENT_TIMESTAMP),
('Guy Hospital', 51.5034, -0.0884, 'London', 'Great Maze Pond, London SE1 9RT', 700, CURRENT_TIMESTAMP),
('Kings College Hospital', 51.4683, -0.0924, 'London', 'Denmark Hill, London SE5 9RS', 900, CURRENT_TIMESTAMP),
('Royal London Hospital', 51.5176, -0.0592, 'London', 'Whitechapel Rd, London E1 1BB', 650, CURRENT_TIMESTAMP),
('St Georges Hospital', 51.4268, -0.1736, 'London', 'Blackshaw Rd, Tooting, London SW17 0QT', 750, CURRENT_TIMESTAMP),
('Chelsea and Westminster Hospital', 51.4843, -0.1881, 'London', '369 Fulham Rd, London SW10 9NH', 400, CURRENT_TIMESTAMP),

-- Hôpitaux de Birmingham
('Queen Elizabeth Hospital Birmingham', 52.4504, -1.9453, 'Birmingham', 'Mindelsohn Way, Edgbaston, Birmingham B15 2WB', 1000, CURRENT_TIMESTAMP),
('Birmingham Children Hospital', 52.4788, -1.9063, 'Birmingham', 'Steelhouse Ln, Birmingham B4 6NH', 300, CURRENT_TIMESTAMP),
('Heartlands Hospital', 52.4844, -1.8147, 'Birmingham', 'Bordesley Green E, Birmingham B9 5SS', 500, CURRENT_TIMESTAMP),

-- Hôpitaux de Leeds
('Leeds General Infirmary', 53.8046, -1.5509, 'Leeds', 'Great George St, Leeds LS1 3EX', 800, CURRENT_TIMESTAMP),
('St James University Hospital', 53.8167, -1.5167, 'Leeds', 'Beckett St, Leeds LS9 7TF', 700, CURRENT_TIMESTAMP),

-- Hôpitaux de Liverpool
('Royal Liverpool University Hospital', 53.4084, -2.9916, 'Liverpool', 'Prescot St, Liverpool L7 8XP', 600, CURRENT_TIMESTAMP),
('Alder Hey Children Hospital', 53.4189, -2.8903, 'Liverpool', 'E Prescot Rd, Liverpool L14 5AB', 350, CURRENT_TIMESTAMP),

-- Hôpitaux de Newcastle
('Royal Victoria Infirmary', 54.9778, -1.6119, 'Newcastle upon Tyne', 'Queen Victoria Rd, Newcastle upon Tyne NE1 4LP', 650, CURRENT_TIMESTAMP),
('Freeman Hospital', 54.9889, -1.5778, 'Newcastle upon Tyne', 'Freeman Rd, High Heaton, Newcastle upon Tyne NE7 7DN', 500, CURRENT_TIMESTAMP),

-- Hôpitaux de Sheffield
('Northern General Hospital', 53.4167, -1.4500, 'Sheffield', 'Herries Rd, Sheffield S5 7AU', 700, CURRENT_TIMESTAMP),
('Royal Hallamshire Hospital', 53.3833, -1.4833, 'Sheffield', 'Glossop Rd, Sheffield S10 2JF', 400, CURRENT_TIMESTAMP),

-- Hôpitaux de Bristol
('Bristol Royal Infirmary', 51.4569, -2.6025, 'Bristol', 'Upper Maudlin St, Bristol BS2 8HW', 600, CURRENT_TIMESTAMP),
('Southmead Hospital', 51.5000, -2.6000, 'Bristol', 'Southmead Rd, Westbury on Trym, Bristol BS10 5NB', 550, CURRENT_TIMESTAMP),

-- Hôpitaux de Cardiff
('University Hospital of Wales', 51.4833, -3.1833, 'Cardiff', 'Heath Park, Cardiff CF14 4XW', 800, CURRENT_TIMESTAMP),
('Royal Gwent Hospital', 51.5833, -2.9833, 'Newport', 'Stow Hill, Newport NP20 2UB', 400, CURRENT_TIMESTAMP),

-- Hôpitaux d'Édimbourg
('Royal Infirmary of Edinburgh', 55.9167, -3.2167, 'Edinburgh', '51 Little France Cres, Edinburgh EH16 4SA', 900, CURRENT_TIMESTAMP),
('Western General Hospital', 55.9667, -3.2167, 'Edinburgh', 'Crewe Rd S, Edinburgh EH4 2XU', 500, CURRENT_TIMESTAMP),

-- Hôpitaux de Glasgow
('Glasgow Royal Infirmary', 55.8667, -4.2333, 'Glasgow', '84 Castle St, Glasgow G4 0SF', 800, CURRENT_TIMESTAMP),
('Queen Elizabeth University Hospital', 55.8667, -4.3167, 'Glasgow', '1345 Govan Rd, Glasgow G51 4TF', 1000, CURRENT_TIMESTAMP);

-- Association des spécialités aux hôpitaux
-- Tous les hôpitaux ont Emergency Medicine
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Emergency Medicine';

-- Hôpitaux avec Cardiology (tous les grands hôpitaux)
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Cardiology' 
AND h.name IN (
    'Stepping Hill Hospital', 'Manchester Royal Infirmary', 'Wythenshawe Hospital', 'North Manchester General Hospital',
    'St Thomas Hospital', 'Guy Hospital', 'Kings College Hospital', 'Royal London Hospital', 'St Georges Hospital',
    'Queen Elizabeth Hospital Birmingham', 'Heartlands Hospital', 'Leeds General Infirmary', 'St James University Hospital',
    'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Freeman Hospital', 'Northern General Hospital',
    'Royal Hallamshire Hospital', 'Bristol Royal Infirmary', 'Southmead Hospital', 'University Hospital of Wales',
    'Royal Gwent Hospital', 'Royal Infirmary of Edinburgh', 'Western General Hospital', 'Glasgow Royal Infirmary',
    'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Neurology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Neurology' 
AND h.name IN (
    'Manchester Royal Infirmary', 'Wythenshawe Hospital', 'St Thomas Hospital', 'Kings College Hospital',
    'Queen Elizabeth Hospital Birmingham', 'Leeds General Infirmary', 'Royal Liverpool University Hospital',
    'Royal Victoria Infirmary', 'Northern General Hospital', 'Bristol Royal Infirmary', 'University Hospital of Wales',
    'Royal Infirmary of Edinburgh', 'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Oncology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Oncology' 
AND h.name IN (
    'Manchester Royal Infirmary', 'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham',
    'Leeds General Infirmary', 'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh', 'Glasgow Royal Infirmary',
    'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Pediatrics (tous les hôpitaux pour enfants + grands hôpitaux généraux)
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Pediatrics' 
AND h.name IN (
    'Birmingham Children Hospital', 'Alder Hey Children Hospital', 'Manchester Royal Infirmary',
    'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham', 'Leeds General Infirmary',
    'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Orthopedics
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Orthopedics' 
AND h.name IN (
    'Stepping Hill Hospital', 'Manchester Royal Infirmary', 'Wythenshawe Hospital', 'St Thomas Hospital',
    'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham', 'Leeds General Infirmary',
    'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Gynecology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Gynecology' 
AND h.name IN (
    'Stepping Hill Hospital', 'Manchester Royal Infirmary', 'Wythenshawe Hospital', 'St Thomas Hospital',
    'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham', 'Leeds General Infirmary',
    'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Urology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Urology' 
AND h.name IN (
    'Manchester Royal Infirmary', 'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham',
    'Leeds General Infirmary', 'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Ophthalmology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Ophthalmology' 
AND h.name IN (
    'Manchester Royal Infirmary', 'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham',
    'Leeds General Infirmary', 'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Dermatology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Dermatology' 
AND h.name IN (
    'Manchester Royal Infirmary', 'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham',
    'Leeds General Infirmary', 'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Psychiatry
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Psychiatry' 
AND h.name IN (
    'Manchester Royal Infirmary', 'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham',
    'Leeds General Infirmary', 'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Anesthesiology (tous les hôpitaux)
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Anesthesiology';

-- Hôpitaux avec Radiology (tous les hôpitaux)
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Radiology';

-- Hôpitaux avec Immunology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Immunology' 
AND h.name IN (
    'Manchester Royal Infirmary', 'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham',
    'Leeds General Infirmary', 'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);

-- Hôpitaux avec Endocrinology
INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Endocrinology' 
AND h.name IN (
    'Manchester Royal Infirmary', 'St Thomas Hospital', 'Kings College Hospital', 'Queen Elizabeth Hospital Birmingham',
    'Leeds General Infirmary', 'Royal Liverpool University Hospital', 'Royal Victoria Infirmary', 'Northern General Hospital',
    'Bristol Royal Infirmary', 'University Hospital of Wales', 'Royal Infirmary of Edinburgh',
    'Glasgow Royal Infirmary', 'Queen Elizabeth University Hospital'
);
