package com.doul.dealz.repository;

import com.doul.dealz.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
