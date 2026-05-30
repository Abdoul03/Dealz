package com.doul.dealz.repository;

import com.doul.dealz.models.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, String> {
}
