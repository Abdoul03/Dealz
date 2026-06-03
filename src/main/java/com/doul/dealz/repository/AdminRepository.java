package com.doul.dealz.repository;

import com.doul.dealz.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Administrateur, String> {
}
