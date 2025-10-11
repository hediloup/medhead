package com.medhead.poc.repository;

import com.medhead.poc.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for medical specialties management.
 */
@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    
    /**
     * Finds a specialty by its exact name.
     */
    Optional<Speciality> findByName(String name);
    
    /**
     * Finds a specialty by its name (case insensitive).
     */
    Optional<Speciality> findByNameIgnoreCase(String name);
    
    /**
     * Finds all specialties whose name contains the given text.
     */
    List<Speciality> findByNameContainingIgnoreCase(String name);
    
    /**
     * Finds all specialties sorted by name.
     */
    List<Speciality> findAllByOrderByNameAsc();
    
    /**
     * Finds all specialties available in a given hospital.
     */
    @Query("SELECT s FROM Speciality s JOIN s.hospitals h WHERE h.id = :hospitalId ORDER BY s.name")
    List<Speciality> findByHospitalId(@Param("hospitalId") Long hospitalId);
    
    /**
     * Finds all specialties available in a given city.
     */
    @Query("SELECT DISTINCT s FROM Speciality s JOIN s.hospitals h WHERE h.city = :city ORDER BY s.name")
    List<Speciality> findByCity(@Param("city") String city);
    
    /**
     * Checks if a specialty exists by its name.
     */
    boolean existsByName(String name);
    
    /**
     * Checks if a specialty exists by its name (case insensitive).
     */
    boolean existsByNameIgnoreCase(String name);
}
