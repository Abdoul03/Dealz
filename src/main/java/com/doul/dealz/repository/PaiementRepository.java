package com.doul.dealz.repository;

import com.doul.dealz.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, String> {
}
