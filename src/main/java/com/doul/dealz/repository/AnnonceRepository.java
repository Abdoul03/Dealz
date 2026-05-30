package com.doul.dealz.repository;

import com.doul.dealz.models.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnonceRepository extends JpaRepository<Annonce, String> {
}
