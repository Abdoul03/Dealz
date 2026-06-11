package com.doul.dealz.repository;

import com.doul.dealz.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, String> {
    List<Avis> findByCibleId(String cibleId);
    boolean existsByAuteurIdAndTransactionId(String auteurId, String transactionId);
    List<Avis> findByTransactionId(String transactionId);
}
