package com.doul.dealz.config;

import com.doul.dealz.model.dto.request.AdminRequestDTO;
import com.doul.dealz.services.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    @Value("${admin.default.email}")
    private String email;

    @Value("${admin.default.password}")
    private String password;

    @Value("${admin.default.nom}")
    private String nom;

    @Value("${admin.default.prenom}")
    private String prenom;

    @Value("${admin.default.telephone}")
    private String telephone;

    private final AdminService adminService;

    @Override
    public void run(ApplicationArguments args) {
        if (adminService.existsAdmin()) {
            log.info("✓ Admin déjà présent — initialisation ignorée.");
            return;
        }

        log.warn("⚠ Aucun administrateur trouvé. Création du compte admin par défaut...");
        adminService.createAdmin(
                new AdminRequestDTO(nom, prenom, email, telephone, password, "SUPER")
        );
        log.info("✓ Admin par défaut créé → email: {} | mot de passe: {}", email, password);
        log.warn("→ Pensez à changer le mot de passe admin en production !");
    }
}
