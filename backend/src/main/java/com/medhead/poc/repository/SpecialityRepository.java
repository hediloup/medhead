package com.medhead.poc.repository;

import com.medhead.poc.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des spécialités médicales.
 */
@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    
    /**
     * Trouve une spécialité par son nom exact.
     */
    Optional<Speciality> findByName(String name);
    
    /**
     * Trouve une spécialité par son nom (insensible à la casse).
     */
    Optional<Speciality> findByNameIgnoreCase(String name);
    
    /**
     * Trouve toutes les spécialités dont le nom contient le texte donné.
     */
    List<Speciality> findByNameContainingIgnoreCase(String name);
    
    /**
     * Trouve toutes les spécialités triées par nom.
     */
    List<Speciality> findAllByOrderByNameAsc();
    
    /**
     * Trouve toutes les spécialités disponibles dans un hôpital donné.
     */
    @Query("SELECT s FROM Speciality s JOIN s.hospitals h WHERE h.id = :hospitalId ORDER BY s.name")
    List<Speciality> findByHospitalId(@Param("hospitalId") Long hospitalId);
    
    /**
     * Trouve toutes les spécialités disponibles dans une ville donnée.
     */
    @Query("SELECT DISTINCT s FROM Speciality s JOIN s.hospitals h WHERE h.city = :city ORDER BY s.name")
    List<Speciality> findByCity(@Param("city") String city);
    
    /**
     * Vérifie si une spécialité existe par son nom.
     */
    boolean existsByName(String name);
    
    /**
     * Vérifie si une spécialité existe par son nom (insensible à la casse).
     */
    boolean existsByNameIgnoreCase(String name);
}
