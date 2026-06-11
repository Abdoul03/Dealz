package com.doul.dealz.services;

import com.doul.dealz.model.Categorie;
import com.doul.dealz.model.dto.mapper.CategorieMapper;
import com.doul.dealz.model.dto.request.CategorieRequestDTO;
import com.doul.dealz.repository.CategorieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieService {
    private final CategorieRepository categorieRepository;
    private final CategorieMapper categorieMapper;

    public Categorie createCategorie(CategorieRequestDTO categorieRequestDTO) {
        Categorie categorie = categorieMapper.toCategorieEntity(categorieRequestDTO);
        categorieRepository.save(categorie);
        return categorie;
    }

    public List <Categorie> getAllCategorie() {
        List<Categorie> categorie = categorieRepository.findAll();
        return categorie;
    }

    public Categorie getAnCategorie (String id ) {
        Categorie categorie = categorieRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Categorie introuvable")
        );
        return categorie;
    }

    public Categorie updateCategorie (String id,CategorieRequestDTO categorieRequestDTO) {
        Categorie categorie = categorieRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Categorie introuvable")
        );

        categorie.setNom(categorieRequestDTO.nom());

        Categorie updateCategorie = categorieRepository.save(categorie);

        return updateCategorie;
    }

    public boolean deleteCategorie (String id) {
        Categorie categorie = categorieRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Categorie introuvable")
        );

        categorieRepository.delete(categorie);
        return true;
    }
}
