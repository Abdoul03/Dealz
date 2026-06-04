package com.doul.dealz.repository;

import com.doul.dealz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByTelephone(String telephone);
    Optional<User> findByEmail(String email);
}
