package com.medhead.poc.repository;

import com.medhead.poc.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for hospital data management.
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    
    /**
     * Finds hospitals that have the requested specialty and available beds.
     */
    @Query("SELECT DISTINCT h FROM Hospital h JOIN h.specialities s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialty, '%')) AND h.availableBeds > 0")
    List<Hospital> findBySpecialtyAndAvailableBeds(@Param("specialty") String specialty);
    
    /**
     * Finds all hospitals with available beds.
     */
    List<Hospital> findByAvailableBedsGreaterThan(Integer minBeds);
    
    /**
     * Finds hospitals by name.
     */
    List<Hospital> findByNameContainingIgnoreCase(String name);
}
