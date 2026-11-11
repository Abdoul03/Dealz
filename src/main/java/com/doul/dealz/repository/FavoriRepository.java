package com.doul.dealz.repository;

import com.doul.dealz.model.Favori;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriRepository extends JpaRepository<Favori, String> {
    List<Favori> findByUtilisateurIdOrderByDateAjoutDesc(String utilisateurId);

    Optional<Favori> findByUtilisateurIdAndAnnonceId(String utilisateurId, String annonceId);

    boolean existsByUtilisateurIdAndAnnonceId(String utilisateurId, String annonceId);

    void deleteByUtilisateurIdAndAnnonceId(String utilisateurId, String annonceId);

    void deleteByAnnonceId(String annonceId);
}
