package com.medhead.poc.testdata;

import com.medhead.poc.model.Hospital;
import com.medhead.poc.model.Speciality;
import com.medhead.poc.repository.HospitalRepository;
import com.medhead.poc.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("test")
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeTestData();
    }

    private void initializeTestData() {
        // Create test specialties
        Speciality cardiology = createSpeciality("Cardiology", "Cardiology and cardiovascular diseases");
        Speciality neurology = createSpeciality("Neurology", "Neurology and nervous system diseases");
        Speciality emergency = createSpeciality("Emergency Medicine", "Emergency medical care");
        Speciality surgery = createSpeciality("General Surgery", "General surgical procedures");

        // Save specialties
        specialityRepository.saveAll(Arrays.asList(cardiology, neurology, emergency, surgery));

        // Create test hospitals
        Hospital hospital1 = createHospital(
            "St George's Hospital",
            51.4253, -0.1780,
            "London",
            "Blackshaw Rd, London SW17 0QT",
            25,
            Arrays.asList(cardiology, emergency)
        );

        Hospital hospital2 = createHospital(
            "King's College Hospital",
            51.4686, -0.0994,
            "London",
            "Denmark Hill, London SE5 9RS",
            30,
            Arrays.asList(cardiology, neurology, emergency)
        );

        Hospital hospital3 = createHospital(
            "Guy's Hospital",
            51.5044, -0.0865,
            "London",
            "Great Maze Pond, London SE1 9RT",
            28,
            Arrays.asList(neurology, surgery)
        );

        Hospital hospital4 = createHospital(
            "Manchester Royal Infirmary",
            53.4592, -2.2264,
            "Manchester",
            "Oxford Rd, Manchester M13 9WL",
            35,
            Arrays.asList(cardiology, emergency, surgery)
        );

        Hospital hospital5 = createHospital(
            "Birmingham Children's Hospital",
            52.4854, -1.8983,
            "Birmingham",
            "Steelhouse Ln, Birmingham B4 6NH",
            15,
            Arrays.asList(emergency)
        );

        // Save hospitals
        hospitalRepository.saveAll(Arrays.asList(hospital1, hospital2, hospital3, hospital4, hospital5));
    }

    private Speciality createSpeciality(String name, String description) {
        Speciality speciality = new Speciality();
        speciality.setName(name);
        speciality.setDescription(description);
        return speciality;
    }

    private Hospital createHospital(String name, double latitude, double longitude, 
                                  String city, String address, int availableBeds, 
                                  java.util.List<Speciality> specialities) {
        Hospital hospital = new Hospital();
        hospital.setName(name);
        hospital.setLatitude(latitude);
        hospital.setLongitude(longitude);
        hospital.setCity(city);
        hospital.setAddress(address);
        hospital.setAvailableBeds(availableBeds);
        
        Set<Speciality> specialitySet = new HashSet<>(specialities);
        hospital.setSpecialities(specialitySet);
        
        return hospital;
    }
}
