-- Script simple pour tester le chargement des données
INSERT INTO specialities (name, description, created_at) VALUES 
('Cardiology', 'Cardiology specialty', CURRENT_TIMESTAMP);

INSERT INTO hospitals (name, latitude, longitude, city, address, available_beds, created_at) VALUES 
('Stepping Hill Hospital', 53.3969, -2.1333, 'Stockport', 'Poplar Grove, Stockport SK2 7JE', 450, CURRENT_TIMESTAMP),
('Manchester Royal Infirmary', 53.4598, -2.2270, 'Manchester', 'Oxford Rd, Manchester M13 9WL', 800, CURRENT_TIMESTAMP);

INSERT INTO hospital_specialities (hospital_id, speciality_id) 
SELECT h.id, s.id 
FROM hospitals h, specialities s 
WHERE s.name = 'Cardiology' AND h.name IN ('Stepping Hill Hospital', 'Manchester Royal Infirmary');
