package com.doul.dealz.repository;

import com.doul.dealz.model.Transaction;
import com.doul.dealz.model.enums.StatutTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByAcheteurIdOrderByDateTransactionDesc(String acheteurId);

    @Query("SELECT t FROM Transaction t WHERE t.annonce.vendeur.id = :vendeurId ORDER BY t.dateTransaction DESC")
    List<Transaction> findByVendeurId(@Param("vendeurId") String vendeurId);

    boolean existsByAnnonceIdAndStatutIn(String annonceId, List<StatutTransaction> statuts);
}
