package com.doul.dealz.repository;

import com.doul.dealz.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie, String> {
    boolean existsByNomIgnoreCase(String nom);
    Optional<Categorie> findByNomIgnoreCase(String nom);
}
