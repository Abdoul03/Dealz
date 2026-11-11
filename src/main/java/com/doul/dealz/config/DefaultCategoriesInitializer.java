package com.doul.dealz.config;

import com.doul.dealz.model.Categorie;
import com.doul.dealz.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultCategoriesInitializer implements ApplicationRunner {

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Vêtements",
            "Chaussures",
            "Sacs",
            "Accessoires"
    );

    private final CategorieRepository categorieRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String nom : DEFAULT_CATEGORIES) {
            if (!categorieRepository.existsByNomIgnoreCase(nom)) {
                Categorie categorie = new Categorie();
                categorie.setNom(nom);
                categorieRepository.save(categorie);
            }
        }
    }
}
