package com.medhead.poc.repository;

import com.medhead.poc.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des données des hôpitaux.
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    
    /**
     * Trouve les hôpitaux qui ont la spécialité demandée et des lits disponibles.
     */
    @Query("SELECT h FROM Hospital h WHERE LOWER(h.specialties) LIKE LOWER(CONCAT('%', :specialty, '%')) AND h.availableBeds > 0")
    List<Hospital> findBySpecialtyAndAvailableBeds(@Param("specialty") String specialty);
    
    /**
     * Trouve tous les hôpitaux avec des lits disponibles.
     */
    List<Hospital> findByAvailableBedsGreaterThan(Integer minBeds);
    
    /**
     * Trouve les hôpitaux par nom.
     */
    List<Hospital> findByNameContainingIgnoreCase(String name);
}
