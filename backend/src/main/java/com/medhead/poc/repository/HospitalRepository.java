package com.medhead.poc.repository;

import com.medhead.poc.model.Hospital;
import com.medhead.poc.dto.HospitalProjection;
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
     * Optimized query using projection for better performance.
     * Returns only required fields to reduce memory usage and improve query speed.
     */
    @Query("SELECT h.id as id, h.name as name, s.name as specialty, h.latitude as latitude, h.longitude as longitude, h.availableBeds as availableBeds " +
           "FROM Hospital h JOIN h.specialities s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialty, '%')) AND h.availableBeds > 0")
    List<HospitalProjection> findAvailableHospitalsBySpecialtyOptimized(@Param("specialty") String specialty);
    
    /**
     * Alias for findAvailableHospitalsBySpecialtyOptimized - for consistency with new code
     */
    @Query("SELECT h.id as id, h.name as name, s.name as specialty, h.latitude as latitude, h.longitude as longitude, h.availableBeds as availableBeds " +
           "FROM Hospital h JOIN h.specialities s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialty, '%')) AND h.availableBeds > 0")
    List<HospitalProjection> findBySpecialtyAndAvailableBedsProjection(@Param("specialty") String specialty);
    
    /**
     * Finds all hospitals with available beds.
     */
    List<Hospital> findByAvailableBedsGreaterThan(Integer minBeds);
    
    /**
     * Finds hospitals by name.
     */
    List<Hospital> findByNameContainingIgnoreCase(String name);
}
