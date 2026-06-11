package com.doul.dealz.repository;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.enums.StatutAnnonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnnonceRepository extends JpaRepository<Annonce, String> {

    List<Annonce> findByStatutOrderByDatePublicationDesc(StatutAnnonce statut);

    List<Annonce> findByVendeurIdOrderByDatePublicationDesc(String vendeurId);

    List<Annonce> findByStatutAndCategorieIdOrderByDatePublicationDesc(StatutAnnonce statut, String categorieId);

    Optional<Annonce> findByIdAndVendeurId(String id, String vendeurId);

    List<Annonce> findByStatutAndVendeurIdOrderByDatePublicationDesc(StatutAnnonce statut, String vendeurId);

    @Query("SELECT a FROM Annonce a WHERE a.statut = :statut AND " +
           "(LOWER(a.titre) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :motCle, '%'))) " +
           "ORDER BY a.datePublication DESC")
    List<Annonce> searchByMotCle(@Param("statut") StatutAnnonce statut, @Param("motCle") String motCle);
}
