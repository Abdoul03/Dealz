package com.doul.dealz.config;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.Categorie;
import com.doul.dealz.model.User;
import com.doul.dealz.model.enums.EtatArticle;
import com.doul.dealz.model.enums.StatutAnnonce;
import com.doul.dealz.repository.AnnonceRepository;
import com.doul.dealz.repository.CategorieRepository;
import com.doul.dealz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoDataInitializer implements ApplicationRunner {

    private static final String SELLER_EMAIL = "vendeur.demo@dealz.ml";
    private static final String BUYER_EMAIL = "acheteur.demo@dealz.ml";
    private static final String DEMO_PASSWORD = "DealzDemo2026!";

    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;
    private final AnnonceRepository annonceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${dealz.seed-demo-data:false}")
    private boolean seedDemoData;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedDemoData) return;

        User seller = getOrCreateUser(SELLER_EMAIL, "Traoré", "Aïssata", "+22370000001");
        getOrCreateUser(BUYER_EMAIL, "Diarra", "Moussa", "+22370000002");

        List<DemoListing> listings = List.of(
                new DemoListing("Chemise bogolan", "Faso Créa", "M", "Chemise en bogolan cousue à Bamako, portée quelques fois.", 8000, EtatArticle.BON_ETAT, "Vêtements", "ACI 2000", "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=1000&q=80"),
                new DemoListing("Veste en jean", "Levi's", "L", "Veste en jean épaisse, propre et sans accroc.", 18000, EtatArticle.TRES_BON_ETAT, "Vêtements", "Badalabougou", "https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=1000&q=80"),
                new DemoListing("Baskets blanches", "Nike", "42", "Baskets d'occasion, semelles en bon état. Essayage possible au rendez-vous.", 22000, EtatArticle.BON_ETAT, "Chaussures", "Hippodrome", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=1000&q=80"),
                new DemoListing("Sac à main", "Sans marque", null, "Sac à main marron avec plusieurs poches intérieures.", 10000, EtatArticle.TRES_BON_ETAT, "Sacs", "Faladié", "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80"),
                new DemoListing("Lunettes de soleil", "Ray-Ban", null, "Lunettes de soleil d'occasion avec leur étui.", 12000, EtatArticle.BON_ETAT, "Accessoires", "Kalaban Coura", "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=1000&q=80"),
                new DemoListing("Robe imprimée", "Création locale", "38", "Robe légère en tissu imprimé, idéale pour la saison chaude.", 9000, EtatArticle.TRES_BON_ETAT, "Vêtements", "Sogoniko", "https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=1000&q=80")
        );

        int created = 0;
        for (DemoListing item : listings) {
            if (annonceRepository.findByVendeurIdOrderByDatePublicationDesc(seller.getId()).stream()
                    .anyMatch(existing -> existing.getTitre().equals(item.titre()))) continue;

            Categorie categorie = categorieRepository.findByNomIgnoreCase(item.categorie())
                    .orElseGet(() -> {
                        Categorie newCategory = new Categorie();
                        newCategory.setNom(item.categorie());
                        return categorieRepository.save(newCategory);
                    });

            Annonce annonce = new Annonce();
            annonce.setTitre(item.titre());
            annonce.setMarque(item.marque());
            annonce.setTaille(item.taille());
            annonce.setDescription(item.description());
            annonce.setPrix(item.prix());
            annonce.setUrlImage(item.imageUrl());
            annonce.setUrlImages(List.of(item.imageUrl()));
            annonce.setEtat(item.etat());
            annonce.setStatut(StatutAnnonce.PUBLIEE);
            annonce.setDatePublication(LocalDateTime.now().minusHours(created * 3L));
            annonce.setQuartier(item.quartier());
            annonce.setVendeur(seller);
            annonce.setCategorie(categorie);
            annonceRepository.save(annonce);
            created++;
        }

        log.info("Données de démonstration Dealz prêtes : {} annonce(s) ajoutée(s).", created);
        log.info("Comptes de démonstration Dealz initialisés.");
    }

    private User getOrCreateUser(String email, String nom, String prenom, String telephone) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            user.setTelephone(telephone);
            user.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
            user.setLocalisation("Bamako");
            return userRepository.save(user);
        });
    }

    private record DemoListing(
            String titre,
            String marque,
            String taille,
            String description,
            float prix,
            EtatArticle etat,
            String categorie,
            String quartier,
            String imageUrl
    ) {}
}
