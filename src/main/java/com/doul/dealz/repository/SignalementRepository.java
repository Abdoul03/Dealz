package com.doul.dealz.repository;

import com.doul.dealz.model.Signalement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignalementRepository extends JpaRepository<Signalement, String> {
    List<Signalement> findBySignaleurId(String signaleurId);
}
