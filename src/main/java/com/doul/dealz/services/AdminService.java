package com.doul.dealz.services;

import com.doul.dealz.model.Administrateur;
import com.doul.dealz.model.dto.mapper.UserMapper;
import com.doul.dealz.model.dto.request.AdminRequestDTO;
import com.doul.dealz.model.dto.response.UserResponseDTO;
import com.doul.dealz.model.enums.TypeCompte;
import com.doul.dealz.repository.AdminRepository;
import com.doul.dealz.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createAdmin(AdminRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        if (userRepository.findByTelephone(dto.telephone()).isPresent()) {
            throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé.");
        }

        Administrateur admin = new Administrateur();
        admin.setNom(dto.nom());
        admin.setPrenom(dto.prenom());
        admin.setEmail(dto.email());
        admin.setTelephone(dto.telephone());
        admin.setPassword(passwordEncoder.encode(dto.password()));
        admin.setTypeCompte(TypeCompte.Aministrateur);
        admin.setNiveau(dto.niveau() != null ? dto.niveau() : "SUPER");
        admin.setDateCreation(LocalDate.now());

        return userMapper.toUserResponse(adminRepository.save(admin));
    }

    public boolean existsAdmin() {
        return adminRepository.count() > 0;
    }
}
