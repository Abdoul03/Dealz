package com.doul.dealz.repository;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.enums.StatutAnnonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Modifying;

public interface AnnonceRepository extends JpaRepository<Annonce, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Annonce a WHERE a.id = :id")
    Optional<Annonce> findByIdForUpdate(@Param("id") String id);

    List<Annonce> findByVendeurIdOrderByDatePublicationDesc(String vendeurId);

    Optional<Annonce> findByIdAndVendeurId(String id, String vendeurId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Annonce a SET a.statut = :nouveauStatut, a.datePublication = :datePublication " +
            "WHERE a.id = :annonceId AND a.statut = :statutActuel")
    int changerStatutSiActuel(
            @Param("annonceId") String annonceId,
            @Param("statutActuel") StatutAnnonce statutActuel,
            @Param("nouveauStatut") StatutAnnonce nouveauStatut,
            @Param("datePublication") java.time.LocalDateTime datePublication);

    List<Annonce> findByStatutAndVendeurIdOrderByDatePublicationDesc(StatutAnnonce statut, String vendeurId);

    @Query("SELECT a FROM Annonce a WHERE a.statut = :statut " +
            "AND (:categorieId IS NULL OR a.categorie.id = :categorieId) " +
            "AND (:motCle IS NULL OR :motCle = '' OR " +
            "LOWER(a.titre) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(COALESCE(a.marque, '')) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(COALESCE(a.taille, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))) " +
            "AND (:taille IS NULL OR LOWER(COALESCE(a.taille, '')) LIKE LOWER(CONCAT('%', :taille, '%'))) " +
            "AND (:prixMin IS NULL OR a.prix >= :prixMin) " +
            "AND (:prixMax IS NULL OR a.prix <= :prixMax) " +
            "ORDER BY a.datePublication DESC")
    Slice<Annonce> findAvailableWithFilters(
            @Param("statut") StatutAnnonce statut,
            @Param("categorieId") String categorieId,
            @Param("motCle") String motCle,
            @Param("taille") String taille,
            @Param("prixMin") Float prixMin,
            @Param("prixMax") Float prixMax,
            Pageable pageable);
}
