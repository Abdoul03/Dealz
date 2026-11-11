package com.doul.dealz.services;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.Favori;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.response.AnnonceResponseDTO;
import com.doul.dealz.repository.AnnonceRepository;
import com.doul.dealz.repository.FavoriRepository;
import com.doul.dealz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriService {

    private final FavoriRepository favoriRepository;
    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final AnnonceService annonceService;

    @Transactional(readOnly = true)
    public List<AnnonceResponseDTO> getMesFavoris(String utilisateurId) {
        return favoriRepository.findByUtilisateurIdOrderByDateAjoutDesc(utilisateurId)
                .stream()
                .map(Favori::getAnnonce)
                .map(annonceService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getMesFavorisIds(String utilisateurId) {
        return favoriRepository.findByUtilisateurIdOrderByDateAjoutDesc(utilisateurId)
                .stream()
                .map(favori -> favori.getAnnonce().getId())
                .toList();
    }

    public void ajouter(String annonceId, String utilisateurId) {
        if (favoriRepository.existsByUtilisateurIdAndAnnonceId(utilisateurId, annonceId)) return;

        User utilisateur = userRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));

        Favori favori = new Favori();
        favori.setUtilisateur(utilisateur);
        favori.setAnnonce(annonce);
        favori.setDateAjout(LocalDateTime.now());
        favoriRepository.save(favori);
    }

    public void retirer(String annonceId, String utilisateurId) {
        favoriRepository.deleteByUtilisateurIdAndAnnonceId(utilisateurId, annonceId);
    }
}
