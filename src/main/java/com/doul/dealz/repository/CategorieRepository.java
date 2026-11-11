package com.doul.dealz.repository;

import com.doul.dealz.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, String> {
    boolean existsByNomIgnoreCase(String nom);
}
